package org.rootservices.authorization.persistence.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ClientResponseType;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.rootservices.authorization.persistence.repository.ResponseTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 8/9/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class ClientResponseTypeMapperTest {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ResponseTypeRepository responseTypeRepository;
    @Autowired
    private ClientResponseTypeMapper subject;

    private Client insertClient() throws URISyntaxException {
        Client client = new Client(
                UUID.randomUUID(),
                new URI("https://rootservices.org/continue")
        );
        clientRepository.insert(client);
        return client;
    }

    @Test
    public void testInsert() throws Exception {
        Client client = insertClient();
        ResponseType responseType = responseTypeRepository.getByName("CODE");

        ClientResponseType clientResponseType = new ClientResponseType(UUID.randomUUID(), responseType, client);

        subject.insert(clientResponseType);
    }
}