package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.RefreshToken;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.entity.TokenChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 10/8/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class TokenChainMapperTest {
    @Autowired
    private TokenChainMapper subject;
    @Autowired
    private TokenMapper tokenMapper;
    @Autowired
    private RefreshTokenMapper refreshTokenMapper;

    public Token loadToken(String accessToken) {
        Token token = FixtureFactory.makeOAuthToken(accessToken);
        tokenMapper.insert(token);
        return token;
    }

    public RefreshToken loadRefreshToken(String refreshAccessToken, Token token, Token headToken) {
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(refreshAccessToken, token, headToken);
        refreshTokenMapper.insert(refreshToken);
        return refreshToken;
    }

    @Test
    public void insertShouldBeOk() {
        String accessToken = "access-token";
        String headAccessToken = "head-access-token";
        String prevAccessToken = "prev-access-token";
        String refreshAccessToken = "refresh-access-token";

        Token token = loadToken(accessToken);
        Token previousToken = loadToken(prevAccessToken);

        Token headToken = loadToken(headAccessToken);
        RefreshToken refreshToken = loadRefreshToken(refreshAccessToken, previousToken, headToken);

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
        assertThat(actual.getToken().getExpiresAt(), is(token.getExpiresAt()));

        assertThat(actual.getPreviousToken().getId(), is(previousToken.getId()));
        assertThat(actual.getPreviousToken().getToken(), is(previousToken.getToken()));
        assertThat(actual.getPreviousToken().isRevoked(), is(false));
        assertThat(actual.getPreviousToken().getGrantType(), is(previousToken.getGrantType()));
        assertThat(actual.getPreviousToken().getCreatedAt(), is(notNullValue()));
        assertThat(actual.getPreviousToken().getExpiresAt(), is(previousToken.getExpiresAt()));

        assertThat(actual.getRefreshToken().getId(), is(refreshToken.getId()));
        assertThat(actual.getRefreshToken().getTokenId(), is(refreshToken.getTokenId()));
        assertThat(actual.getRefreshToken().getAccessToken(), is(refreshToken.getAccessToken()));
        assertThat(actual.getRefreshToken().getExpiresAt(), is(refreshToken.getExpiresAt()));
        assertThat(actual.getRefreshToken().getCreatedAt(), is(notNullValue()));
    }

    @Test(expected = DuplicateKeyException.class)
    public void insertShouldThrowDuplicateKeyException() {
        String accessToken = "access-token";
        String headAccessToken = "head-access-token";
        String prevAccessToken = "prev-access-token";
        String refreshAccessToken = "refresh-access-token";

        Token token1 = loadToken(accessToken);
        Token previousToken1 = loadToken(prevAccessToken);

        Token headToken = loadToken(headAccessToken);
        RefreshToken refreshToken = loadRefreshToken(refreshAccessToken, previousToken1, headToken);

        TokenChain tokenChain1 = new TokenChain();
        tokenChain1.setId(UUID.randomUUID());
        tokenChain1.setToken(token1);
        tokenChain1.setPreviousToken(previousToken1);
        tokenChain1.setRefreshToken(refreshToken);

        subject.insert(tokenChain1);

        String accessToken2 = "access-token";
        String prevAccessToken2 = "prev-access-token";
        Token token2 = loadToken(accessToken2);
        Token previousToken2 = loadToken(prevAccessToken2);

        TokenChain tokenChain2 = new TokenChain();
        tokenChain2.setId(UUID.randomUUID());
        tokenChain2.setToken(token2);
        tokenChain2.setPreviousToken(previousToken2);
        tokenChain2.setRefreshToken(refreshToken);

        // should not allow insert b/c refreshToken was already inserted in tc1
        subject.insert(tokenChain2);
    }
}