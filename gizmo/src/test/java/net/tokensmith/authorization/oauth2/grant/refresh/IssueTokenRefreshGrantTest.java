package net.tokensmith.authorization.oauth2.grant.refresh;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.authorization.oauth2.grant.refresh.exception.CompromisedRefreshTokenException;
import net.tokensmith.authorization.oauth2.grant.token.builder.TokenResponseBuilder;
import net.tokensmith.authorization.oauth2.grant.token.entity.Extension;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenGraph;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenResponse;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenType;
import net.tokensmith.authorization.persistence.entity.*;
import net.tokensmith.authorization.persistence.exceptions.DuplicateRecordException;
import net.tokensmith.authorization.persistence.repository.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 10/8/16.
 */
public class IssueTokenRefreshGrantTest {
    private IssueTokenRefreshGrant subject;

    @Mock
    private InsertTokenGraphRefreshGrant mockInsertTokenGraphRefreshGrant;
    @Mock
    private TokenChainRepository mockTokenChainRepository;
    @Mock
    private ResourceOwnerTokenRepository mockResourceOwnerTokenRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        subject = new IssueTokenRefreshGrant(
                mockInsertTokenGraphRefreshGrant,
                mockTokenChainRepository,
                mockResourceOwnerTokenRepository,
                new TokenResponseBuilder(),
                "https://sso.rootservices.org"
        );
    }

    @Test
    public void runShouldBeOk() throws Exception {
        UUID clientId = UUID.randomUUID();
        List<Client> audience = FixtureFactory.makeAudience(clientId);
        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        UUID previousTokenId = UUID.randomUUID();
        UUID refreshTokenId = UUID.randomUUID();

        List<Scope> scopes = FixtureFactory.makeOpenIdScopes();

        String headAccessToken = "head-access-token";
        Token headToken = FixtureFactory.makeOpenIdToken(headAccessToken, clientId, new ArrayList<>());
        headToken.setCreatedAt(OffsetDateTime.now().minusDays(1));

        TokenGraph tokenGraph = FixtureFactory.makeTokenGraph(clientId, audience);
        when(mockInsertTokenGraphRefreshGrant.insertTokenGraph(clientId, scopes, headToken, audience)).thenReturn(tokenGraph);

        ArgumentCaptor<TokenChain> tokenChainCaptor = ArgumentCaptor.forClass(TokenChain.class);
        ArgumentCaptor<ResourceOwnerToken> resourceOwnerTokenCaptor = ArgumentCaptor.forClass(ResourceOwnerToken.class);

        TokenResponse actual = subject.run(clientId, resourceOwner.getId(), previousTokenId, refreshTokenId, headToken, scopes, audience);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessToken(), is(tokenGraph.getPlainTextAccessToken()));
        assertThat(actual.getRefreshAccessToken(), is(tokenGraph.getPlainTextRefreshToken().get()));
        assertThat(actual.getExpiresIn(), is(3600L));
        assertThat(actual.getTokenType(), is(TokenType.BEARER));
        assertThat(actual.getExtension(), is(Extension.IDENTITY));

        assertThat(actual.getTokenClaims(), is(notNullValue()));
        assertThat(actual.getTokenClaims().getIssuer(), is(notNullValue()));
        assertThat(actual.getTokenClaims().getIssuer(), is("https://sso.rootservices.org"));
        assertThat(actual.getTokenClaims().getAudience(), is(notNullValue()));
        assertThat(actual.getTokenClaims().getAudience().size(), is(1));
        assertThat(actual.getTokenClaims().getAudience().get(0), is(clientId.toString()));
        assertThat(actual.getTokenClaims().getIssuedAt(), is(notNullValue()));
        assertThat(actual.getTokenClaims().getExpirationTime(), is(notNullValue()));
        assertThat(actual.getTokenClaims().getAuthTime(), is(headToken.getCreatedAt().toEpochSecond()));

        verify(mockTokenChainRepository, times(1)).insert(tokenChainCaptor.capture());
        TokenChain tokenChain = tokenChainCaptor.getValue();
        assertThat(tokenChain, is(notNullValue()));
        assertThat(tokenChain.getId(), is(notNullValue()));
        assertThat(tokenChain.getToken().getId(), is(tokenGraph.getToken().getId()));
        assertThat(tokenChain.getPreviousToken().getId(), is(previousTokenId));
        assertThat(tokenChain.getRefreshToken().getId(), is(refreshTokenId));

        verify(mockResourceOwnerTokenRepository, times(1)).insert(resourceOwnerTokenCaptor.capture());
        ResourceOwnerToken actualRot = resourceOwnerTokenCaptor.getValue();
        assertThat(actualRot.getId(), is(notNullValue()));
        assertThat(actualRot.getToken(), is(tokenGraph.getToken()));
        assertThat(actualRot.getResourceOwner().getId(), is(resourceOwner.getId()));
    }

    @Test
    public void runWhenRefreshTokenUsedShouldThrowCompromisedRefreshTokenException() throws Exception {
        UUID clientId = UUID.randomUUID();
        List<Client> audience = FixtureFactory.makeAudience(clientId);
        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        UUID previousTokenId = UUID.randomUUID();
        UUID refreshTokenId = UUID.randomUUID();

        List<Scope> scopes = FixtureFactory.makeOpenIdScopes();

        String headAccessToken = "head-access-token";
        Token headToken = FixtureFactory.makeOpenIdToken(headAccessToken, clientId, new ArrayList<>());

        TokenGraph tokenGraph = FixtureFactory.makeTokenGraph(clientId, new ArrayList<>());
        when(mockInsertTokenGraphRefreshGrant.insertTokenGraph(clientId, scopes, headToken, audience)).thenReturn(tokenGraph);

        DuplicateRecordException dre = new DuplicateRecordException("", null);
        doThrow(dre).when(mockTokenChainRepository).insert(any(TokenChain.class));

        CompromisedRefreshTokenException actual = null;
        try {
            subject.run(clientId, resourceOwner.getId(), previousTokenId, refreshTokenId, headToken, scopes, audience);
        } catch (CompromisedRefreshTokenException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(dre));
        assertThat(actual.getMessage(), is("refresh token was already used"));
    }

}