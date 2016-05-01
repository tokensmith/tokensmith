package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadConfidentialClientTokenReady;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 4/19/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class ResourceOwnerTokenMapperTest {

    @Autowired
    private LoadConfidentialClientTokenReady loadConfidentialClientTokenReady;
    @Autowired
    private RandomString randomString;
    @Autowired
    private TokenMapper tokenMapper;
    @Autowired
    private TokenScopeMapper tokenScopeMapper;
    @Autowired
    private ResourceOwnerTokenMapper subject;

    @Test
    public void insertShouldBeOk() throws Exception {
        // begin prepare db for test
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);
        Token token = FixtureFactory.makeToken(authCode.getUuid());
        tokenMapper.insert(token);

        Scope scope = authCode.getAccessRequest().getAccessRequestScopes().get(0).getScope();
        TokenScope tokenScope = new TokenScope();
        tokenScope.setId(UUID.randomUUID());
        tokenScope.setTokenId(token.getUuid());
        tokenScope.setScope(scope);
        tokenScopeMapper.insert(tokenScope);
        // end prepare db for test

        ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();
        resourceOwnerToken.setId(UUID.randomUUID());
        ResourceOwner ro = new ResourceOwner();
        ro.setUuid(authCode.getAccessRequest().getResourceOwnerUUID());
        resourceOwnerToken.setResourceOwner(ro);
        resourceOwnerToken.setToken(token);

        subject.insert(resourceOwnerToken);

        // make sure it was inserted.
        ResourceOwnerToken actual = subject.getByAccessToken(token.getToken());
        assertThat(actual, is(notNullValue()));
    }


    @Test
    public void getByAccessTokenShouldBeOk() throws Exception {
        // begin prepare db for test
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);
        Token token = FixtureFactory.makeToken(authCode.getUuid());
        tokenMapper.insert(token);

        Scope scope = authCode.getAccessRequest().getAccessRequestScopes().get(0).getScope();
        TokenScope tokenScope = new TokenScope();
        tokenScope.setId(UUID.randomUUID());
        tokenScope.setTokenId(token.getUuid());
        tokenScope.setScope(scope);
        tokenScopeMapper.insert(tokenScope);

        ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();
        resourceOwnerToken.setId(UUID.randomUUID());
        ResourceOwner ro = new ResourceOwner();
        ro.setUuid(authCode.getAccessRequest().getResourceOwnerUUID());
        resourceOwnerToken.setResourceOwner(ro);
        resourceOwnerToken.setToken(token);
        subject.insert(resourceOwnerToken);
        // end prepare db for test

        ResourceOwnerToken actual = subject.getByAccessToken(token.getToken());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(resourceOwnerToken.getId()));

        assertThat(actual.getResourceOwner(), is(notNullValue()));
        assertThat(actual.getResourceOwner().getUuid(), is(ro.getUuid()));
        assertThat(actual.getResourceOwner().getEmail(), is(notNullValue()));
        assertThat(actual.getResourceOwner().getCreatedAt(), is(notNullValue()));

        assertThat(actual.getToken(), is(notNullValue()));
        assertThat(actual.getToken().getUuid(), is(token.getUuid()));
        assertThat(actual.getToken().isRevoked(), is(false));
        assertThat(actual.getToken().getGrantType(), is(GrantType.AUTHORIZATION_CODE));
        assertThat(actual.getToken().getCreatedAt(), is(notNullValue()));
        assertThat(actual.getToken().getExpiresAt(), is(notNullValue()));

        assertThat(actual.getToken().getTokenScopes().size(), is(1));
        assertThat(actual.getToken().getTokenScopes().get(0).getScope().getName(), is("profile"));

        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
    }
}