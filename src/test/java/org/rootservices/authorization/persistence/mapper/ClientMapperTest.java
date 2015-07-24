package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadClientWithScopes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 11/15/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class ClientMapperTest {

    @Autowired
    private LoadClientWithScopes loadClientWithScopes;
    @Autowired
    private ClientMapper subject;

    @Test
    public void insert() throws URISyntaxException {
        Client client = FixtureFactory.makeClientWithScopes();
        subject.insert(client);
    }

    @Test
    public void getByUUID() throws URISyntaxException {
        Client expectedClient = loadClientWithScopes.run();

        Client actualClient = subject.getByUUID(expectedClient.getUuid());

        assertThat(actualClient.getUuid()).isEqualTo(expectedClient.getUuid());
        assertThat(actualClient.getResponseType()).isEqualTo(expectedClient.getResponseType());
        assertThat(actualClient.getCreatedAt()).isNotNull();
        assertThat(actualClient.getScopes().size()).isEqualTo(1);
        assertThat(actualClient.getScopes().get(0).getUuid()).isEqualTo(
                expectedClient.getScopes().get(0).getUuid()
        );
        assertThat(actualClient.getScopes().get(0).getName()).isEqualTo("profile");
    }

    @Test
    public void getByUUIDNotFound() {
        Client actualClient = subject.getByUUID(UUID.randomUUID());

        assertThat(actualClient).isEqualTo(null);
    }
}
