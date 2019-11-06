package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.TestAppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ClientResponseType;
import net.tokensmith.repository.entity.ResponseType;
import net.tokensmith.repository.repo.ClientRepository;
import net.tokensmith.repository.repo.ResponseTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Created by tommackenzie on 8/9/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= TestAppConfig.class, loader= AnnotationConfigContextLoader.class)
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