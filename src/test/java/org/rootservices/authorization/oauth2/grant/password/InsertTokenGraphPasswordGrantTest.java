package org.rootservices.authorization.oauth2.grant.password;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.exception.ServerException;
import org.rootservices.authorization.oauth2.grant.token.MakeBearerToken;
import org.rootservices.authorization.oauth2.grant.token.MakeRefreshToken;
import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenGraph;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.*;
import org.rootservices.authorization.security.RandomString;
import org.springframework.dao.DuplicateKeyException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 11/15/16.
 */
public class InsertTokenGraphPasswordGrantTest {
    private InsertTokenGraphPasswordGrant subject;

    @Mock
    private ConfigurationRepository mockConfigurationRepository;
    @Mock
    private RandomString mockRandomString;
    @Mock
    private MakeBearerToken mockMakeBearerToken;
    @Mock
    private TokenRepository mockTokenRepository;
    @Mock
    private MakeRefreshToken mockMakeRefreshToken;
    @Mock
    private RefreshTokenRepository mockRefreshTokenRepository;
    @Mock
    private TokenScopeRepository mockTokenScopeRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new InsertTokenGraphPasswordGrant(
                mockConfigurationRepository,
                mockRandomString,
                mockMakeBearerToken,
                mockTokenRepository,
                mockMakeRefreshToken,
                mockRefreshTokenRepository,
                mockTokenScopeRepository
        );
    }

    @Test
    public void insertTokenGraphShouldBeOk() throws Exception {
        UUID clientId = UUID.randomUUID();
        List<Scope> scopes = FixtureFactory.makeOpenIdScopes();

        Configuration configuration = FixtureFactory.makeConfiguration();
        when(mockConfigurationRepository.get()).thenReturn(configuration);

        String plainTextToken = "plain-text-token";
        Token token = FixtureFactory.makeOpenIdToken(plainTextToken, clientId);
        token.setCreatedAt(OffsetDateTime.now());

        when(mockMakeBearerToken.run(clientId, plainTextToken, configuration.getAccessTokenPasswordSecondsToExpiry())).thenReturn(token);

        String refreshAccessToken = "refresh-token";
        when(mockRandomString.run(32)).thenReturn(plainTextToken, refreshAccessToken);

        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(refreshAccessToken, token);

        when(mockMakeRefreshToken.run(token, refreshAccessToken, configuration.getRefreshTokenSecondsToExpiry())).thenReturn(refreshToken);

        TokenGraph actual = subject.insertTokenGraph(clientId, scopes);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPlainTextAccessToken(), is(plainTextToken));
        assertThat(actual.getToken(), is(token));
        assertThat(actual.getToken().getGrantType(), is(GrantType.PASSWORD));
        assertThat(actual.getRefreshTokenId().isPresent(), is(true));
        assertThat(actual.getRefreshTokenId().get(), is(refreshToken.getId()));
        assertThat(actual.getPlainTextRefreshToken().isPresent(), is(true));
        assertThat(actual.getPlainTextRefreshToken().get(), is(refreshAccessToken));
        assertThat(actual.getExtension(), is(Extension.IDENTITY));

        // should insert a token
        verify(mockTokenRepository).insert(token);

        // should insert a refresh token.
        verify(mockRefreshTokenRepository, times(1)).insert(refreshToken);

        // should insert token scopes.
        ArgumentCaptor<TokenScope> tokenScopeCaptor = ArgumentCaptor.forClass(TokenScope.class);
        verify(mockTokenScopeRepository, times(1)).insert(tokenScopeCaptor.capture());

        List<TokenScope> actualTokenScopes = tokenScopeCaptor.getAllValues();

        assertThat(actualTokenScopes.get(0).getId(), is(notNullValue()));
        assertThat(actualTokenScopes.get(0).getTokenId(), is(token.getId()));
        assertThat(actualTokenScopes.get(0).getScope(), is(scopes.get(0)));
    }

    @Test
    public void handleDuplicateTokenShouldRetry() throws Exception {
        UUID clientId = UUID.randomUUID();

        List<Scope> scopes = FixtureFactory.makeOpenIdScopes();

        Configuration configuration = FixtureFactory.makeConfiguration();
        when(mockConfigurationRepository.get()).thenReturn(configuration);

        String plainTextToken = "plain-text-token";
        Token token = FixtureFactory.makeOpenIdToken(plainTextToken, clientId);
        token.setCreatedAt(OffsetDateTime.now());

        when(mockMakeBearerToken.run(clientId, plainTextToken, configuration.getAccessTokenPasswordSecondsToExpiry())).thenReturn(token);

        String refreshAccessToken = "refresh-token";
        when(mockRandomString.run(32)).thenReturn(plainTextToken, refreshAccessToken);

        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(refreshAccessToken, token);
        when(mockMakeRefreshToken.run(token, refreshAccessToken, configuration.getRefreshTokenSecondsToExpiry())).thenReturn(refreshToken);

        // force a retry.
        DuplicateKeyException dke = new DuplicateKeyException("test");
        DuplicateRecordException dre = new DuplicateRecordException("test", dke, Optional.of("token"));
        doThrow(dre).doNothing().when(mockTokenRepository).insert(any(Token.class));
        when(mockRandomString.run(33)).thenReturn(plainTextToken);

        TokenGraph actual = subject.insertTokenGraph(clientId, scopes);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPlainTextAccessToken(), is(plainTextToken));
        assertThat(actual.getToken(), is(token));
        assertThat(actual.getToken().getGrantType(), is(GrantType.PASSWORD));
        assertThat(actual.getRefreshTokenId().isPresent(), is(true));
        assertThat(actual.getRefreshTokenId().get(), is(refreshToken.getId()));
        assertThat(actual.getPlainTextRefreshToken().isPresent(), is(true));
        assertThat(actual.getPlainTextRefreshToken().get(), is(refreshAccessToken));
        assertThat(actual.getExtension(), is(Extension.IDENTITY));

        // should have updated configuration.
        verify(mockConfigurationRepository).updateAccessTokenSize(configuration.getId(), 33);

        // should insert a token
        verify(mockTokenRepository, times(2)).insert(token);
        // should insert a refresh token.
        verify(mockRefreshTokenRepository, times(1)).insert(refreshToken);

        // should insert token scopes.
        ArgumentCaptor<TokenScope> tokenScopeCaptor = ArgumentCaptor.forClass(TokenScope.class);
        verify(mockTokenScopeRepository, times(1)).insert(tokenScopeCaptor.capture());

        List<TokenScope> actualTokenScopes = tokenScopeCaptor.getAllValues();

        assertThat(actualTokenScopes.get(0).getId(), is(notNullValue()));
        assertThat(actualTokenScopes.get(0).getTokenId(), is(token.getId()));
        assertThat(actualTokenScopes.get(0).getScope(), is(scopes.get(0)));
    }

    @Test
    public void handleDuplicateTokenWhenKeyIsTokenAttemptIs3ShouldThrowServerException() throws Exception {
        UUID clientId = UUID.randomUUID();
        DuplicateKeyException dke = new DuplicateKeyException("msg");
        DuplicateRecordException dre = new DuplicateRecordException("msg", dke, Optional.of("token"));
        Integer attempt = 3;
        UUID configId = UUID.randomUUID();
        Integer tokenSize = 32;
        Long secondsToExpiration = 3600L;

        ServerException actual = null;
        try {
            subject.handleDuplicateToken(
                    dre, attempt, clientId, configId, tokenSize, secondsToExpiration
            );
        } catch (ServerException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Failed to insert token. Attempted 3 times. Token size is, 32."));
        assertThat(actual.getCause(), is(dre));
    }

    @Test
    public void handleDuplicateTokenWhenKeyIsEmptyAttemptIs2ShouldThrowServerException() throws Exception {
        UUID clientId = UUID.randomUUID();
        DuplicateKeyException dke = new DuplicateKeyException("msg");
        DuplicateRecordException dre = new DuplicateRecordException("msg", dke, Optional.empty());
        Integer attempt = 2;
        UUID configId = UUID.randomUUID();
        Integer tokenSize = 32;
        Long secondsToExpiration = 3600L;

        ServerException actual = null;
        try {
            subject.handleDuplicateToken(
                    dre, attempt, clientId, configId, tokenSize, secondsToExpiration
            );
        } catch (ServerException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Failed to insert token. Unknown key, unknown. Did not retry. Attempted 2 times. Token size is, 32."));
        assertThat(actual.getCause(), is(dre));
    }

    @Test
    public void handleDuplicateTokenWhenKeyIsNotTokenAttemptIs2ShouldThrowServerException() throws Exception {
        UUID clientId = UUID.randomUUID();
        DuplicateKeyException dke = new DuplicateKeyException("msg");
        DuplicateRecordException dre = new DuplicateRecordException("msg", dke, Optional.of("foo"));
        Integer attempt = 2;
        UUID configId = UUID.randomUUID();
        Integer tokenSize = 32;
        Long secondsToExpiration = 3600L;

        ServerException actual = null;
        try {
            subject.handleDuplicateToken(
                    dre, attempt, clientId, configId, tokenSize, secondsToExpiration
            );
        } catch (ServerException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Failed to insert token. Unknown key, foo. Did not retry. Attempted 2 times. Token size is, 32."));
        assertThat(actual.getCause(), is(dre));
    }

    @Test
    public void handleDuplicateRefreshTokenShouldRetry() throws Exception {
        UUID clientId = UUID.randomUUID();

        List<Scope> scopes = FixtureFactory.makeOpenIdScopes();

        Configuration configuration = FixtureFactory.makeConfiguration();
        when(mockConfigurationRepository.get()).thenReturn(configuration);

        String plainTextToken = "plain-text-token";
        Token token = FixtureFactory.makeOpenIdToken(plainTextToken, clientId);
        token.setCreatedAt(OffsetDateTime.now());

        when(mockMakeBearerToken.run(clientId, plainTextToken, configuration.getAccessTokenPasswordSecondsToExpiry())).thenReturn(token);

        String refreshAccessToken = "refresh-token";
        when(mockRandomString.run(32)).thenReturn(plainTextToken, refreshAccessToken);

        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(refreshAccessToken, token);
        when(mockMakeRefreshToken.run(token, refreshAccessToken, configuration.getRefreshTokenSecondsToExpiry())).thenReturn(refreshToken);

        // force a retry.
        DuplicateKeyException dke = new DuplicateKeyException("test");
        DuplicateRecordException dre = new DuplicateRecordException("test", dke, Optional.of("access_token"));
        doThrow(dre).doNothing().when(mockRefreshTokenRepository).insert(any(RefreshToken.class));
        when(mockRandomString.run(33)).thenReturn(refreshAccessToken);

        TokenGraph actual = subject.insertTokenGraph(clientId, scopes);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPlainTextAccessToken(), is(plainTextToken));
        assertThat(actual.getToken(), is(token));
        assertThat(actual.getToken().getGrantType(), is(GrantType.PASSWORD));
        assertThat(actual.getRefreshTokenId().isPresent(), is(true));
        assertThat(actual.getRefreshTokenId().get(), is(refreshToken.getId()));
        assertThat(actual.getPlainTextRefreshToken().isPresent(), is(true));
        assertThat(actual.getPlainTextRefreshToken().get(), is(refreshAccessToken));
        assertThat(actual.getExtension(), is(Extension.IDENTITY));

        // should have updated configuration.
        verify(mockConfigurationRepository).updateRefreshTokenSize(configuration.getId(), 33);

        // should insert a token
        verify(mockTokenRepository, times(1)).insert(token);
        // should insert a refresh token.
        verify(mockRefreshTokenRepository, times(2)).insert(refreshToken);

        // should insert token scopes.
        ArgumentCaptor<TokenScope> tokenScopeCaptor = ArgumentCaptor.forClass(TokenScope.class);
        verify(mockTokenScopeRepository, times(1)).insert(tokenScopeCaptor.capture());

        List<TokenScope> actualTokenScopes = tokenScopeCaptor.getAllValues();

        assertThat(actualTokenScopes.get(0).getId(), is(notNullValue()));
        assertThat(actualTokenScopes.get(0).getTokenId(), is(token.getId()));
        assertThat(actualTokenScopes.get(0).getScope(), is(scopes.get(0)));
    }

    @Test
    public void handleDuplicateRefreshTokenWhenKeyIsAccessTokenAttemptIs3ShouldThrowServerException() throws Exception {
        UUID clientId = UUID.randomUUID();

        DuplicateKeyException dke = new DuplicateKeyException("msg");
        DuplicateRecordException dre = new DuplicateRecordException("msg", dke, Optional.of("access_token"));
        Integer attempt = 3;
        UUID configId = UUID.randomUUID();
        Integer tokenSize = 32;
        TokenGraph tokenGraph = FixtureFactory.makeTokenGraph(clientId);
        Long secondsToExpiration = 1209600L;

        ServerException actual = null;
        try {
            subject.handleDuplicateRefreshToken(
                    dre, attempt, configId, tokenSize, secondsToExpiration, tokenGraph
            );
        } catch (ServerException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Failed to insert refresh_token. Attempted 2 times. Token size is, 32."));
        assertThat(actual.getCause(), is(dre));

        verify(mockTokenRepository, times(1)).revokeById(tokenGraph.getToken().getId());
    }

    @Test
    public void handleDuplicateRefreshTokenWhenKeyIsEmptyAttemptIs2ShouldThrowServerException() throws Exception {
        UUID clientId = UUID.randomUUID();

        DuplicateKeyException dke = new DuplicateKeyException("msg");
        DuplicateRecordException dre = new DuplicateRecordException("msg", dke, Optional.empty());
        Integer attempt = 3;
        UUID configId = UUID.randomUUID();
        Integer tokenSize = 32;
        TokenGraph tokenGraph = FixtureFactory.makeTokenGraph(clientId);
        Long secondsToExpiration = 1209600L;

        ServerException actual = null;
        try {
            subject.handleDuplicateRefreshToken(
                    dre, attempt, configId, tokenSize, secondsToExpiration, tokenGraph
            );
        } catch (ServerException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Failed to insert refresh_token. Unknown key, unknown. Did not retry. Attempted 2 times. Token size is, 32."));
        assertThat(actual.getCause(), is(dre));

        verify(mockTokenRepository, times(1)).revokeById(tokenGraph.getToken().getId());
    }

    @Test
    public void handleDuplicateRefreshTokenWhenKeyIsNotTokenAttemptIs2ShouldThrowServerException() throws Exception {
        UUID clientId = UUID.randomUUID();

        DuplicateKeyException dke = new DuplicateKeyException("msg");
        DuplicateRecordException dre = new DuplicateRecordException("msg", dke, Optional.of("foo"));
        Integer attempt = 3;
        UUID configId = UUID.randomUUID();
        Integer tokenSize = 32;
        TokenGraph tokenGraph = FixtureFactory.makeTokenGraph(clientId);
        Long secondsToExpiration = 1209600L;

        ServerException actual = null;
        try {
            subject.handleDuplicateRefreshToken(
                    dre, attempt, configId, tokenSize, secondsToExpiration, tokenGraph
            );
        } catch (ServerException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Failed to insert refresh_token. Unknown key, foo. Did not retry. Attempted 2 times. Token size is, 32."));
        assertThat(actual.getCause(), is(dre));

        verify(mockTokenRepository, times(1)).revokeById(tokenGraph.getToken().getId());
    }

}