package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.TestAppConfig;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.RefreshToken;
import net.tokensmith.repository.entity.Token;
import net.tokensmith.repository.entity.TokenChain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by tommackenzie on 10/8/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= TestAppConfig.class, loader= AnnotationConfigContextLoader.class)
@Transactional
public class TokenChainMapperTest {
    @Autowired
    private TokenChainMapper subject;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private TokenMapper tokenMapper;
    @Autowired
    private RefreshTokenMapper refreshTokenMapper;

    public Token loadToken(String accessToken, UUID clientId) {
        Token token = FixtureFactory.makeOAuthToken(accessToken, clientId, new ArrayList<>());
        tokenMapper.insert(token);
        return token;
    }

    public RefreshToken loadRefreshToken(String refreshAccessToken, Token token) {
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(refreshAccessToken, token);
        refreshTokenMapper.insert(refreshToken);
        return refreshToken;
    }

    @Test
    public void insertShouldBeOk() throws Exception {
        String accessToken = "access-token";
        String prevAccessToken = "prev-access-token";
        String refreshAccessToken = "refresh-access-token";

        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        clientMapper.insert(client);

        Token token = loadToken(accessToken, client.getId());
        Token previousToken = loadToken(prevAccessToken, client.getId());

        RefreshToken refreshToken = loadRefreshToken(refreshAccessToken, previousToken);

        TokenChain tokenChain = new TokenChain();
        tokenChain.setId(UUID.randomUUID());
        tokenChain.setToken(token);
        tokenChain.setPreviousToken(previousToken);
        tokenChain.setRefreshToken(refreshToken);

        subject.insert(tokenChain);

        TokenChain actual = subject.getById(tokenChain.getId());

        assertThat(actual.getId(), is(tokenChain.getId()));

        assertThat(actual.getToken().getId(), is(token.getId()));
        assertThat(actual.getToken().getToken(), is(token.getToken()));
        assertThat(actual.getToken().isRevoked(), is(false));
        assertThat(actual.getToken().getGrantType(), is(token.getGrantType()));
        assertThat(actual.getToken().getCreatedAt(), is(notNullValue()));
        assertThat(actual.getToken().getExpiresAt().toEpochSecond(), is(token.getExpiresAt().toEpochSecond()));

        assertThat(actual.getPreviousToken().getId(), is(previousToken.getId()));
        assertThat(actual.getPreviousToken().getToken(), is(previousToken.getToken()));
        assertThat(actual.getPreviousToken().isRevoked(), is(false));
        assertThat(actual.getPreviousToken().getGrantType(), is(previousToken.getGrantType()));
        assertThat(actual.getPreviousToken().getCreatedAt(), is(notNullValue()));
        assertThat(actual.getPreviousToken().getExpiresAt().toEpochSecond(), is(previousToken.getExpiresAt().toEpochSecond()));

        assertThat(actual.getRefreshToken().getId(), is(refreshToken.getId()));
        assertThat(actual.getRefreshToken().getTokenId(), is(refreshToken.getTokenId()));
        assertThat(actual.getRefreshToken().getAccessToken(), is(refreshToken.getAccessToken()));
        assertThat(actual.getRefreshToken().getExpiresAt().toEpochSecond(), is(refreshToken.getExpiresAt().toEpochSecond()));
        assertThat(actual.getRefreshToken().getCreatedAt(), is(notNullValue()));
    }

    @Test(expected = DuplicateKeyException.class)
    public void insertShouldThrowDuplicateKeyException() throws Exception {
        String accessToken = "access-token";
        String prevAccessToken = "prev-access-token";
        String refreshAccessToken = "refresh-access-token";

        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        clientMapper.insert(client);

        Token token1 = loadToken(accessToken, client.getId());
        Token previousToken1 = loadToken(prevAccessToken, client.getId());

        RefreshToken refreshToken = loadRefreshToken(refreshAccessToken, previousToken1);

        TokenChain tokenChain1 = new TokenChain();
        tokenChain1.setId(UUID.randomUUID());
        tokenChain1.setToken(token1);
        tokenChain1.setPreviousToken(previousToken1);
        tokenChain1.setRefreshToken(refreshToken);

        subject.insert(tokenChain1);

        String accessToken2 = "access-token";
        String prevAccessToken2 = "prev-access-token";
        Token token2 = loadToken(accessToken2, client.getId());
        Token previousToken2 = loadToken(prevAccessToken2, client.getId());

        TokenChain tokenChain2 = new TokenChain();
        tokenChain2.setId(UUID.randomUUID());
        tokenChain2.setToken(token2);
        tokenChain2.setPreviousToken(previousToken2);
        tokenChain2.setRefreshToken(refreshToken);

        // should not allow insert b/c refreshToken was already inserted in tc1
        subject.insert(tokenChain2);
    }
}