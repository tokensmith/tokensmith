package org.rootservices.authorization.persistence.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;
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
public class ClientMapperTest {

    @Autowired
    private ClientMapper subject;

    public Client insertClient() throws URISyntaxException{
        UUID uuid = UUID.randomUUID();
        ResponseType rt = ResponseType.CODE;
        URI redirectURI = new URI("https://rootservices.org");
        Client client = new Client(uuid, rt, redirectURI);

        subject.insert(client);
        return client;
    }

    @Test
    @Transactional
    public void insert() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();
        ResponseType rt = ResponseType.CODE;
        URI redirectURI = new URI("https://rootservices.org");
        Client client = new Client(uuid, rt, redirectURI);

        subject.insert(client);
    }

    @Test
    @Transactional
    public void getByUUID() throws URISyntaxException {
        Client expectedClient = insertClient();
        Client actualClient = subject.getByUUID(expectedClient.getUuid());

        assertThat(actualClient.getUuid()).isEqualTo(expectedClient.getUuid());
        assertThat(actualClient.getResponseType()).isEqualTo(expectedClient.getResponseType());
        assertThat(actualClient.getCreatedAt()).isNotNull();
    }

    @Test
    @Transactional
    public void getByUUIDAuthUserNotFound() {
        Client actualClient = subject.getByUUID(UUID.randomUUID());

        assertThat(actualClient).isEqualTo(null);
    }
}
