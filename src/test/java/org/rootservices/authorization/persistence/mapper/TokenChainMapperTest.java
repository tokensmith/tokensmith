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

    public Token loadToken() {
        Token token = FixtureFactory.makeOAuthToken();
        tokenMapper.insert(token);
        return token;
    }

    public RefreshToken loadRefreshToken(UUID tokenId) {
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(tokenId);
        refreshTokenMapper.insert(refreshToken);
        return refreshToken;
    }

    @Test
    public void insertShouldBeOk() {
        Token nextToken = loadToken();
        Token previousToken = loadToken();
        RefreshToken refreshToken = loadRefreshToken(previousToken.getId());

        TokenChain tokenChain = new TokenChain();
        tokenChain.setId(UUID.randomUUID());
        tokenChain.setNextToken(nextToken);
        tokenChain.setPreviousToken(previousToken);
        tokenChain.setRefreshToken(refreshToken);

        subject.insert(tokenChain);

        TokenChain actual = subject.getById(tokenChain.getId());

        assertThat(actual.getId(), is(tokenChain.getId()));

        assertThat(actual.getNextToken().getId(), is(nextToken.getId()));
        assertThat(actual.getNextToken().getToken(), is(nextToken.getToken()));
        assertThat(actual.getNextToken().isRevoked(), is(false));
        assertThat(actual.getNextToken().getGrantType(), is(nextToken.getGrantType()));
        assertThat(actual.getNextToken().getCreatedAt(), is(notNullValue()));
        assertThat(actual.getNextToken().getExpiresAt(), is(nextToken.getExpiresAt()));

        assertThat(actual.getPreviousToken().getId(), is(previousToken.getId()));
        assertThat(actual.getPreviousToken().getToken(), is(previousToken.getToken()));
        assertThat(actual.getPreviousToken().isRevoked(), is(false));
        assertThat(actual.getPreviousToken().getGrantType(), is(previousToken.getGrantType()));
        assertThat(actual.getPreviousToken().getCreatedAt(), is(notNullValue()));
        assertThat(actual.getPreviousToken().getExpiresAt(), is(previousToken.getExpiresAt()));

        assertThat(actual.getRefreshToken().getId(), is(refreshToken.getId()));
        assertThat(actual.getRefreshToken().getTokenId(), is(refreshToken.getTokenId()));
        assertThat(actual.getRefreshToken().getToken(), is(refreshToken.getToken()));
        assertThat(actual.getRefreshToken().getExpiresAt(), is(refreshToken.getExpiresAt()));
        assertThat(actual.getRefreshToken().getCreatedAt(), is(notNullValue()));
    }

    @Test(expected = DuplicateKeyException.class)
    public void insertShouldThrowDuplicateKeyException() {
        Token nextToken1 = loadToken();
        Token previousToken1 = loadToken();
        RefreshToken refreshToken = loadRefreshToken(previousToken1.getId());

        TokenChain tokenChain1 = new TokenChain();
        tokenChain1.setId(UUID.randomUUID());
        tokenChain1.setNextToken(nextToken1);
        tokenChain1.setPreviousToken(previousToken1);
        tokenChain1.setRefreshToken(refreshToken);

        subject.insert(tokenChain1);

        Token nextToken2 = loadToken();
        Token previousToken2 = loadToken();

        TokenChain tokenChain2 = new TokenChain();
        tokenChain2.setId(UUID.randomUUID());
        tokenChain2.setNextToken(nextToken2);
        tokenChain2.setPreviousToken(previousToken2);
        tokenChain2.setRefreshToken(refreshToken);

        // should not allow insert b/c refreshToken was already inserted in tc1
        subject.insert(tokenChain2);
    }
}