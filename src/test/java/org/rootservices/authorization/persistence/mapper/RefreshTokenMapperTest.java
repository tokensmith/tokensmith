package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.RefreshToken;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.security.RandomString;
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
 * Created by tommackenzie on 10/2/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class RefreshTokenMapperTest {
    @Autowired
    private RefreshTokenMapper subject;
    @Autowired
    private TokenMapper tokenMapper;
    @Autowired
    private RandomString randomString;

    public UUID loadToken() {
        Token token = FixtureFactory.makeOAuthToken();
        tokenMapper.insert(token);
        return token.getId();
    }

    @Test
    public void insertShouldBeOk() throws Exception {
        UUID tokenId = loadToken();
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(tokenId);

        subject.insert(refreshToken);

        String accessToken = new String(refreshToken.getToken());
        RefreshToken actual = subject.getByToken(accessToken);
        assertThat(actual, is(notNullValue()));
    }

    @Test(expected = DuplicateKeyException.class)
    public void insertShouldThrowDuplicateKeyException() throws Exception {
        UUID tokenId = loadToken();
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(tokenId);

        subject.insert(refreshToken);

        refreshToken.setId(UUID.randomUUID());
        subject.insert(refreshToken);
    }

}