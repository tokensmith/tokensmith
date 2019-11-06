package net.tokensmith.authorization.oauth2.grant.refresh;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.authorization.exception.ServerException;
import net.tokensmith.authorization.oauth2.grant.token.MakeBearerToken;
import net.tokensmith.authorization.oauth2.grant.token.MakeRefreshToken;
import net.tokensmith.authorization.oauth2.grant.token.entity.Extension;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenGraph;
import net.tokensmith.repository.entity.*;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.repo.*;
import net.tokensmith.authorization.security.RandomString;
import org.springframework.dao.DuplicateKeyException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 11/18/16.
 */
public class InsertTokenGraphRefreshGrantTest {
    private InsertTokenGraphRefreshGrant subject;

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
    @Mock
    private TokenLeadTokenRepository mockTokenLeadTokenRepository;
    @Mock
    private TokenAudienceRepository mockTokenAudienceRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new InsertTokenGraphRefreshGrant(
                mockConfigurationRepository,
                mockRandomString,
                mockMakeBearerToken,
                mockTokenRepository,
                mockMakeRefreshToken,
                mockRefreshTokenRepository,
                mockTokenScopeRepository,
                mockTokenLeadTokenRepository,
                mockTokenAudienceRepository
        );
    }

    @Test
    public void insertTokenGraphShouldBeOk() throws Exception {
        UUID clientId = UUID.randomUUID();
        List<Scope> scopes = FixtureFactory.makeOpenIdScopes();
        List<Client> audience = FixtureFactory.makeAudience(clientId);

        Configuration configuration = FixtureFactory.makeConfiguration();
        when(mockConfigurationRepository.get()).thenReturn(configuration);

        String plainTextToken = "plain-text-token";
        Token token = FixtureFactory.makeOpenIdToken(plainTextToken, clientId, new ArrayList<>());
        token.setTokenScopes(new ArrayList<>());
        token.setCreatedAt(OffsetDateTime.now());

        String leadPlainTextToken = "lead-plain-text-token";
        Token leadToken = FixtureFactory.makeOpenIdToken(leadPlainTextToken, clientId, new ArrayList<>());
        leadToken.setCreatedAt(OffsetDateTime.now());

        when(mockMakeBearerToken.run(clientId, plainTextToken, configuration.getAccessTokenRefreshSecondsToExpiry())).thenReturn(token);

        String refreshAccessToken = "refresh-token";
        when(mockRandomString.run(32)).thenReturn(plainTextToken, refreshAccessToken);

        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(refreshAccessToken, token);

        when(mockMakeRefreshToken.run(token, refreshAccessToken, 1209600L)).thenReturn(refreshToken);

        TokenGraph actual = subject.insertTokenGraph(clientId, scopes, leadToken, audience);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPlainTextAccessToken(), is(plainTextToken));
        assertThat(actual.getToken(), is(token));
        assertThat(actual.getToken().getGrantType(), is(GrantType.REFRESSH));
        assertThat(actual.getToken().getAudience(), is(notNullValue()));
        assertThat(actual.getToken().getAudience(), is(audience));

        assertThat(actual.getToken().getTokenScopes(), is(notNullValue()));
        assertThat(actual.getToken().getTokenScopes().size(), is(scopes.size()));

        for(int i=0; i< actual.getToken().getTokenScopes().size(); i++) {
            Scope s = actual.getToken().getTokenScopes().get(i).getScope();
            assertThat(s.getName(), is(s.getName()));
        }

        assertThat(actual.getRefreshTokenId().isPresent(), is(true));
        assertThat(actual.getRefreshTokenId().get(), is(refreshToken.getId()));
        assertThat(actual.getPlainTextRefreshToken().isPresent(), is(true));
        assertThat(actual.getPlainTextRefreshToken().get(), is(refreshAccessToken));
        assertThat(actual.getExtension(), is(Extension.IDENTITY));

        // should insert a token
        verify(mockTokenRepository).insert(token);

        // should insert a token lead token
        ArgumentCaptor<TokenLeadToken> tltArgumentCaptor = ArgumentCaptor.forClass(TokenLeadToken.class);
        verify(mockTokenLeadTokenRepository, times(1)).insert(tltArgumentCaptor.capture());
        assertThat(tltArgumentCaptor.getValue().getId(), is(notNullValue()));
        assertThat(tltArgumentCaptor.getValue().getTokenId(), is(token.getId()));
        assertThat(tltArgumentCaptor.getValue().getLeadTokenId(), is(leadToken.getId()));

        // should insert a refresh token.
        verify(mockRefreshTokenRepository, times(1)).insert(refreshToken);

        // should insert token scopes.
        ArgumentCaptor<TokenScope> tokenScopeCaptor = ArgumentCaptor.forClass(TokenScope.class);
        verify(mockTokenScopeRepository, times(1)).insert(tokenScopeCaptor.capture());

        List<TokenScope> actualTokenScopes = tokenScopeCaptor.getAllValues();
        assertThat(actualTokenScopes.get(0).getId(), is(notNullValue()));
        assertThat(actualTokenScopes.get(0).getTokenId(), is(token.getId()));
        assertThat(actualTokenScopes.get(0).getScope(), is(scopes.get(0)));

        // should insert a token_audience
        ArgumentCaptor<TokenAudience> tokenAudienceCaptor = ArgumentCaptor.forClass(TokenAudience.class);
        verify(mockTokenAudienceRepository, times(1)).insert(tokenAudienceCaptor.capture());

        assertThat(tokenAudienceCaptor.getValue().getId(), is(notNullValue()));
        assertThat(tokenAudienceCaptor.getValue().getTokenId(), is(token.getId()));
        assertThat(tokenAudienceCaptor.getValue().getClientId(), is(clientId));
    }

    @Test
    public void handleDuplicateTokenShouldRetry() throws Exception {
        UUID clientId = UUID.randomUUID();
        List<Scope> scopes = FixtureFactory.makeOpenIdScopes();
        List<Client> audience = FixtureFactory.makeAudience(clientId);

        Configuration configuration = FixtureFactory.makeConfiguration();
        when(mockConfigurationRepository.get()).thenReturn(configuration);

        String plainTextToken = "plain-text-token";
        Token token = FixtureFactory.makeOpenIdToken(plainTextToken, clientId, new ArrayList<>());
        token.setCreatedAt(OffsetDateTime.now());

        when(mockMakeBearerToken.run(clientId, plainTextToken, configuration.getAccessTokenRefreshSecondsToExpiry())).thenReturn(token);

        String refreshAccessToken = "refresh-token";
        when(mockRandomString.run(32)).thenReturn(plainTextToken, refreshAccessToken);

        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(refreshAccessToken, token);
        when(mockMakeRefreshToken.run(token, refreshAccessToken, 1209600L)).thenReturn(refreshToken);

        // force a retry.
        DuplicateKeyException dke = new DuplicateKeyException("test");
        DuplicateRecordException dre = new DuplicateRecordException("test", dke, Optional.of("token"));
        doThrow(dre).doNothing().when(mockTokenRepository).insert(any(Token.class));
        when(mockRandomString.run(33)).thenReturn(plainTextToken);

        TokenGraph actual = subject.insertTokenGraph(clientId, scopes, audience);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPlainTextAccessToken(), is(plainTextToken));
        assertThat(actual.getToken(), is(token));
        assertThat(actual.getToken().getGrantType(), is(GrantType.REFRESSH));
        assertThat(actual.getToken().getAudience(), is(notNullValue()));
        assertThat(actual.getToken().getAudience(), is(audience));
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

        // should insert a token_audience
        ArgumentCaptor<TokenAudience> tokenAudienceCaptor = ArgumentCaptor.forClass(TokenAudience.class);
        verify(mockTokenAudienceRepository, times(1)).insert(tokenAudienceCaptor.capture());

        assertThat(tokenAudienceCaptor.getValue().getId(), is(notNullValue()));
        assertThat(tokenAudienceCaptor.getValue().getTokenId(), is(token.getId()));
        assertThat(tokenAudienceCaptor.getValue().getClientId(), is(clientId));
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
        List<Client> audience = FixtureFactory.makeAudience(clientId);

        Configuration configuration = FixtureFactory.makeConfiguration();
        when(mockConfigurationRepository.get()).thenReturn(configuration);

        String plainTextToken = "plain-text-token";
        Token token = FixtureFactory.makeOpenIdToken(plainTextToken, clientId, new ArrayList<>());
        token.setCreatedAt(OffsetDateTime.now());

        when(mockMakeBearerToken.run(clientId, plainTextToken, configuration.getAccessTokenRefreshSecondsToExpiry())).thenReturn(token);

        String refreshAccessToken = "refresh-token";
        when(mockRandomString.run(32)).thenReturn(plainTextToken, refreshAccessToken);

        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(refreshAccessToken, token);
        when(mockMakeRefreshToken.run(token, refreshAccessToken, 1209600L)).thenReturn(refreshToken);

        // force a retry.
        DuplicateKeyException dke = new DuplicateKeyException("test");
        DuplicateRecordException dre = new DuplicateRecordException("test", dke, Optional.of("access_token"));
        doThrow(dre).doNothing().when(mockRefreshTokenRepository).insert(any(RefreshToken.class));
        when(mockRandomString.run(33)).thenReturn(refreshAccessToken);

        TokenGraph actual = subject.insertTokenGraph(clientId, scopes, audience);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPlainTextAccessToken(), is(plainTextToken));
        assertThat(actual.getToken(), is(token));
        assertThat(actual.getToken().getGrantType(), is(GrantType.REFRESSH));
        assertThat(actual.getToken().getAudience(), is(notNullValue()));
        assertThat(actual.getToken().getAudience(), is(audience));
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
        TokenGraph tokenGraph = FixtureFactory.makeTokenGraph(clientId, new ArrayList<>());
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
        TokenGraph tokenGraph = FixtureFactory.makeTokenGraph(clientId, new ArrayList<>());
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
        TokenGraph tokenGraph = FixtureFactory.makeTokenGraph(clientId, new ArrayList<>());
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