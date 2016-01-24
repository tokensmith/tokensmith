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

import javax.annotation.Resource;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;

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
        ResourceOwner actualUser = subject.getByUUID(expectedUser.getUuid());

        assertThat(actualUser.getUuid()).isEqualTo(expectedUser.getUuid());
        assertThat(actualUser.getEmail()).isEqualTo(expectedUser.getEmail());
        assertThat(actualUser.getPassword()).isEqualTo(expectedUser.getPassword());
        assertThat(actualUser.getCreatedAt()).isNotNull();
        assertThat(actualUser.getCreatedAt()).isInstanceOf(OffsetDateTime.class);
    }

    @Test
    public void getByUUIDAuthUserNotFound() {

        ResourceOwner actualUser = subject.getByUUID(UUID.randomUUID());

        assertThat(actualUser).isEqualTo(null);
    }

    @Test
    public void getByEmail() {

        ResourceOwner expectedUser = insertResourceOwner();
        ResourceOwner actualUser = subject.getByEmail(expectedUser.getEmail());

        assertThat(actualUser.getUuid()).isEqualTo(expectedUser.getUuid());
        assertThat(actualUser.getEmail()).isEqualTo(expectedUser.getEmail());
        assertThat(actualUser.getPassword()).isEqualTo(expectedUser.getPassword());
        assertThat(actualUser.getCreatedAt()).isNotNull();
        assertThat(actualUser.getCreatedAt()).isInstanceOf(OffsetDateTime.class);
    }

    @Test
    public void getByAccessToken() throws DuplicateRecordException, URISyntaxException {
        Token token = loadOpenIdConfidentialClientAll.run();
        ResourceOwner actual = subject.getByAccessToken(token.getToken());

        assertThat(actual).isNotNull();
        assertThat(actual.getUuid()).isNotNull();
        assertThat(actual.getEmail()).isNotNull();
        assertThat(actual.getCreatedAt()).isNotNull();
    }
}
