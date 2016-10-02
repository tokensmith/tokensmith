package org.rootservices.authorization.persistence.mapper;

import helper.fixture.persistence.openid.LoadOpenIdConfidentialClientAll;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


import java.net.URISyntaxException;
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
    private LoadOpenIdConfidentialClientAll loadOpenIdConfidentialClientAll;

    @Autowired
    private ResourceOwnerMapper subject;

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
    public void getByUUID() {
        ResourceOwner expectedUser = insertResourceOwner();
        ResourceOwner actual = subject.getById(expectedUser.getId());

        assertThat(actual.getId(), is(expectedUser.getId()));
        assertThat(actual.getEmail(), is(expectedUser.getEmail()));
        assertThat(actual.getPassword(), is(expectedUser.getPassword()));
        assertThat(actual.isEmailVerified(), is(false));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
    }

    @Test
    public void getByUUIDAuthUserNotFound() {
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
}
