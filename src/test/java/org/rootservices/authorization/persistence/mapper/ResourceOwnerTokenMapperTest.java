package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private ResourceOwnerMapper resourceOwnerMapper;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private TokenMapper tokenMapper;
    @Autowired
    private ScopeMapper scopeMapper;
    @Autowired
    private TokenScopeMapper tokenScopeMapper;
    @Autowired
    private ResourceOwnerTokenMapper subject;

    @Test
    public void insertShouldBeOk() throws Exception {
        // begin prepare db for test
        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        clientMapper.insert(client);

        String accessToken = "access-token";
        Token token = FixtureFactory.makeOpenIdToken(accessToken, client.getId(), new ArrayList<>());
        tokenMapper.insert(token);

        Scope scope = FixtureFactory.makeScope();
        scope.setName("address");
        scopeMapper.insert(scope);

        TokenScope tokenScope = new TokenScope();
        tokenScope.setId(UUID.randomUUID());
        tokenScope.setTokenId(token.getId());
        tokenScope.setScope(scope);
        tokenScopeMapper.insert(tokenScope);
        // end prepare db for test

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        resourceOwnerMapper.insert(resourceOwner);

        ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();
        resourceOwnerToken.setId(UUID.randomUUID());
        ResourceOwner ro = new ResourceOwner();
        ro.setId(resourceOwner.getId());
        resourceOwnerToken.setResourceOwner(ro);
        resourceOwnerToken.setToken(token);

        subject.insert(resourceOwnerToken);

        // make sure it was inserted.
        String hashedAccessToken = new String(token.getToken());
        ResourceOwnerToken actual = subject.getByAccessToken(hashedAccessToken);
        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void getByAccessTokenShouldBeOk() throws Exception {
        // begin prepare db for test
        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        clientMapper.insert(client);

        String accessToken = "access-token";
        Token token = FixtureFactory.makeOpenIdToken(accessToken, client.getId(), new ArrayList<>());
        tokenMapper.insert(token);

        Scope scope = FixtureFactory.makeScope();
        scope.setName("address");
        scopeMapper.insert(scope);

        TokenScope tokenScope = new TokenScope();
        tokenScope.setId(UUID.randomUUID());
        tokenScope.setTokenId(token.getId());
        tokenScope.setScope(scope);
        tokenScopeMapper.insert(tokenScope);

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        resourceOwnerMapper.insert(resourceOwner);

        ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();
        resourceOwnerToken.setId(UUID.randomUUID());
        ResourceOwner ro = new ResourceOwner();
        ro.setId(resourceOwner.getId());
        resourceOwnerToken.setResourceOwner(ro);
        resourceOwnerToken.setToken(token);
        subject.insert(resourceOwnerToken);
        // end prepare db for test

        String hashedAccessToken = new String(token.getToken());
        ResourceOwnerToken actual = subject.getByAccessToken(hashedAccessToken);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(resourceOwnerToken.getId()));

        assertThat(actual.getResourceOwner(), is(notNullValue()));
        assertThat(actual.getResourceOwner().getId(), is(ro.getId()));
        assertThat(actual.getResourceOwner().getEmail(), is(notNullValue()));
        assertThat(actual.getResourceOwner().getCreatedAt(), is(notNullValue()));

        assertThat(actual.getToken(), is(notNullValue()));
        assertThat(actual.getToken().getId(), is(token.getId()));
        assertThat(actual.getToken().isRevoked(), is(false));
        assertThat(actual.getToken().getGrantType(), is(GrantType.AUTHORIZATION_CODE));
        assertThat(actual.getToken().getCreatedAt(), is(notNullValue()));
        assertThat(actual.getToken().getExpiresAt(), is(notNullValue()));

        assertThat(actual.getToken().getTokenScopes().size(), is(1));
        assertThat(actual.getToken().getTokenScopes().get(0).getScope().getName(), is("address"));

        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
    }

}