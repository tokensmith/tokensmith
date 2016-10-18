package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.ResourceOwnerToken;
import org.rootservices.authorization.persistence.entity.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by tommackenzie on 9/25/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class ResourceOwnerMapperTest {
    @Autowired
    private ResourceOwnerMapper subject;
    @Autowired
    private ResourceOwnerTokenMapper resourceOwnerTokenMapper;
    @Autowired
    private TokenMapper tokenMapper;

    public ResourceOwner insertResourceOwner() {
        UUID uuid = UUID.randomUUID();
        byte [] password = "plainTextPassword".getBytes();
        ResourceOwner user = new ResourceOwner(uuid, "test@rootservices.com", password);

        subject.insert(user);
        return user;
    }

    @Test
    public void insert() {
        UUID uuid = UUID.randomUUID();
        byte [] password = "plainTextPassword".getBytes();
        ResourceOwner user = new ResourceOwner(uuid, "test@rootservices.com", password);
        subject.insert(user);
    }

    @Test
    public void getById() {
        ResourceOwner expectedUser = insertResourceOwner();
        ResourceOwner actual = subject.getById(expectedUser.getId());

        assertThat(actual.getId(), is(expectedUser.getId()));
        assertThat(actual.getEmail(), is(expectedUser.getEmail()));
        assertThat(actual.getPassword(), is(expectedUser.getPassword()));
        assertThat(actual.isEmailVerified(), is(false));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
    }

    @Test
    public void getByIdAuthUserNotFound() {
        ResourceOwner actual = subject.getById(UUID.randomUUID());

        assertThat(actual, is(nullValue()));
    }

    @Test
    public void getByEmail() {
        ResourceOwner expectedUser = insertResourceOwner();
        ResourceOwner actual = subject.getByEmail(expectedUser.getEmail());

        assertThat(actual.getId(), is(expectedUser.getId()));
        assertThat(actual.getEmail(), is(expectedUser.getEmail()));
        assertThat(actual.getPassword(), is(expectedUser.getPassword()));
        assertThat(actual.isEmailVerified(), is(false));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
    }

    @Test
    public void getByAccessToken() throws Exception {
        // prepare data for test.
        ResourceOwner expectedUser = insertResourceOwner();

        String accessToken = "access-token";
        Token token = FixtureFactory.makeOpenIdToken(accessToken);
        tokenMapper.insert(token);

        ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();
        resourceOwnerToken.setId(UUID.randomUUID());
        resourceOwnerToken.setResourceOwner(expectedUser);
        resourceOwnerToken.setToken(token);
        resourceOwnerTokenMapper.insert(resourceOwnerToken);
        // end prepare

        String hashedAccessToken = new String(token.getToken());
        ResourceOwner actual = subject.getByAccessToken(hashedAccessToken);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getEmail(), is(expectedUser.getEmail()));
        assertThat(actual.getPassword(), is(expectedUser.getPassword()));
        assertThat(actual.isEmailVerified(), is(false));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
    }
}
