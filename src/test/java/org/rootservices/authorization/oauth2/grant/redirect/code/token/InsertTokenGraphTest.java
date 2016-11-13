package org.rootservices.authorization.oauth2.grant.redirect.code.token;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.exception.ServerException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.entity.TokenGraph;
import org.rootservices.authorization.oauth2.grant.token.MakeBearerToken;
import org.rootservices.authorization.oauth2.grant.token.MakeRefreshToken;
import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.ConfigurationRepository;
import org.rootservices.authorization.persistence.repository.RefreshTokenRepository;
import org.rootservices.authorization.persistence.repository.TokenRepository;
import org.rootservices.authorization.persistence.repository.TokenScopeRepository;
import org.rootservices.authorization.security.RandomString;
import org.springframework.dao.DuplicateKeyException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 11/13/16.
 */
public class InsertTokenGraphTest {
    private InsertTokenGraph subject;

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
        subject = new InsertTokenGraph(
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
        List<AccessRequestScope> ars = FixtureFactory.makeAccessRequestScopes();

        Configuration configuration = FixtureFactory.makeConfiguration();
        when(mockConfigurationRepository.get()).thenReturn(configuration);

        String plainTextToken = "plain-text-token";
        Token token = FixtureFactory.makeOpenIdToken(plainTextToken);
        token.setCreatedAt(OffsetDateTime.now());

        when(mockMakeBearerToken.run(plainTextToken)).thenReturn(token);
        when(mockMakeBearerToken.getSecondsToExpiration()).thenReturn(3600L);

        String refreshAccessToken = "refresh-token";
        when(mockRandomString.run(32)).thenReturn(plainTextToken, refreshAccessToken);

        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(refreshAccessToken, token, token);

        when(mockMakeRefreshToken.run(token, token, refreshAccessToken)).thenReturn(refreshToken);

        TokenGraph actual = subject.insertTokenGraph(ars);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPlainTextAccessToken(), is(plainTextToken));
        assertThat(actual.getToken(), is(token));
        assertThat(actual.getRefreshTokenId(), is(refreshToken.getId()));
        assertThat(actual.getPlainTextRefreshToken(), is(refreshAccessToken));
        assertThat(actual.getExtension(), is(Extension.IDENTITY));

        // should insert a token
        verify(mockTokenRepository).insert(token);
        // should insert a refresh token.
        verify(mockRefreshTokenRepository, times(1)).insert(refreshToken);

        // should insert token scopes.
        ArgumentCaptor<TokenScope> tokenScopeCaptor = ArgumentCaptor.forClass(TokenScope.class);
        verify(mockTokenScopeRepository, times(2)).insert(tokenScopeCaptor.capture());

        List<TokenScope> actualTokenScopes = tokenScopeCaptor.getAllValues();

        assertThat(actualTokenScopes.get(0).getId(), is(notNullValue()));
        assertThat(actualTokenScopes.get(0).getTokenId(), is(token.getId()));
        assertThat(actualTokenScopes.get(0).getScope(), is(ars.get(0).getScope()));

        assertThat(actualTokenScopes.get(1).getId(), is(notNullValue()));
        assertThat(actualTokenScopes.get(1).getTokenId(), is(token.getId()));
        assertThat(actualTokenScopes.get(1).getScope(), is(ars.get(1).getScope()));
    }

    @Test
    public void handleDuplicateTokenShouldRetry() {
        // TODO: implement this.
    }

    @Test
    public void handleDuplicateTokenWhenKeyIsTokenAttemptIs3ShouldThrowServerException() throws Exception {
        DuplicateKeyException dke = new DuplicateKeyException("msg");
        DuplicateRecordException dre = new DuplicateRecordException("msg", dke, Optional.of("token"));
        Integer attempt = 3;
        UUID configId = UUID.randomUUID();
        Integer tokenSize = 32;
        List<AccessRequestScope> ars = new ArrayList<>();

        ServerException actual = null;
        try {
            subject.handleDuplicateToken(
                    dre, attempt, configId, tokenSize, ars
            );
        } catch (ServerException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Failed to insert token. Attempted 2 times. Token size is, 32."));
        assertThat(actual.getCause(), is(dre));
    }

    @Test
    public void handleDuplicateTokenWhenKeyIsEmptyAttemptIs2ShouldThrowServerException() throws Exception {
        DuplicateKeyException dke = new DuplicateKeyException("msg");
        DuplicateRecordException dre = new DuplicateRecordException("msg", dke, Optional.empty());
        Integer attempt = 2;
        UUID configId = UUID.randomUUID();
        Integer tokenSize = 32;
        List<AccessRequestScope> ars = new ArrayList<>();

        ServerException actual = null;
        try {
            subject.handleDuplicateToken(
                    dre, attempt, configId, tokenSize, ars
            );
        } catch (ServerException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Failed to insert token. Unknown key, unknown. Did not retry. Attempted 1 times. Token size is, 32."));
        assertThat(actual.getCause(), is(dre));
    }

    @Test
    public void handleDuplicateTokenWhenKeyIsNotTokenAttemptIs2ShouldThrowServerException() throws Exception {
        DuplicateKeyException dke = new DuplicateKeyException("msg");
        DuplicateRecordException dre = new DuplicateRecordException("msg", dke, Optional.of("foo"));
        Integer attempt = 2;
        UUID configId = UUID.randomUUID();
        Integer tokenSize = 32;
        List<AccessRequestScope> ars = new ArrayList<>();

        ServerException actual = null;
        try {
            subject.handleDuplicateToken(
                    dre, attempt, configId, tokenSize, ars
            );
        } catch (ServerException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Failed to insert token. Unknown key, foo. Did not retry. Attempted 1 times. Token size is, 32."));
        assertThat(actual.getCause(), is(dre));
    }

    // REFRESH
    @Test
    public void handleDuplicateRefreshTokenShouldRetry() {
        // TODOD: implement this.
    }

    @Test
    public void handleDuplicateRefreshTokenWhenKeyIsAccessTokenAttemptIs3ShouldThrowServerException() throws Exception {
        DuplicateKeyException dke = new DuplicateKeyException("msg");
        DuplicateRecordException dre = new DuplicateRecordException("msg", dke, Optional.of("access_token"));
        Integer attempt = 3;
        UUID configId = UUID.randomUUID();
        Integer tokenSize = 32;
        UUID tokenId = UUID.randomUUID();
        List<AccessRequestScope> ars = new ArrayList<>();

        ServerException actual = null;
        try {
            subject.handleDuplicateRefreshToken(
                    dre, attempt, configId, tokenSize, tokenId, ars
            );
        } catch (ServerException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Failed to insert refresh_token. Attempted 2 times. Token size is, 32."));
        assertThat(actual.getCause(), is(dre));

        verify(mockTokenRepository, times(1)).revokeById(tokenId);
    }

    @Test
    public void handleDuplicateRefreshTokenWhenKeyIsEmptyAttemptIs2ShouldThrowServerException() throws Exception {
        DuplicateKeyException dke = new DuplicateKeyException("msg");
        DuplicateRecordException dre = new DuplicateRecordException("msg", dke, Optional.empty());
        Integer attempt = 3;
        UUID configId = UUID.randomUUID();
        Integer tokenSize = 32;
        UUID tokenId = UUID.randomUUID();
        List<AccessRequestScope> ars = new ArrayList<>();

        ServerException actual = null;
        try {
            subject.handleDuplicateRefreshToken(
                    dre, attempt, configId, tokenSize, tokenId, ars
            );
        } catch (ServerException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Failed to insert refresh_token. Unknown key, unknown. Did not retry. Attempted 2 times. Token size is, 32."));
        assertThat(actual.getCause(), is(dre));

        verify(mockTokenRepository, times(1)).revokeById(tokenId);
    }

    @Test
    public void handleDuplicateRefreshTokenWhenKeyIsNotTokenAttemptIs2ShouldThrowServerException() throws Exception {
        DuplicateKeyException dke = new DuplicateKeyException("msg");
        DuplicateRecordException dre = new DuplicateRecordException("msg", dke, Optional.of("foo"));
        Integer attempt = 3;
        UUID configId = UUID.randomUUID();
        Integer tokenSize = 32;
        UUID tokenId = UUID.randomUUID();
        List<AccessRequestScope> ars = new ArrayList<>();

        ServerException actual = null;
        try {
            subject.handleDuplicateRefreshToken(
                    dre, attempt, configId, tokenSize, tokenId, ars
            );
        } catch (ServerException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Failed to insert refresh_token. Unknown key, foo. Did not retry. Attempted 2 times. Token size is, 32."));
        assertThat(actual.getCause(), is(dre));

        verify(mockTokenRepository, times(1)).revokeById(tokenId);
    }
}