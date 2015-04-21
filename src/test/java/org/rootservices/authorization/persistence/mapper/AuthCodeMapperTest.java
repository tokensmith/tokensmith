package org.rootservices.authorization.persistence.mapper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/10/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
public class AuthCodeMapperTest {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ResourceOwnerRepository resourceOwnerRepository;

    @Autowired
    private AuthCodeMapper subject;

    private UUID clientUUID;
    private UUID resourceOwnerUUID;

    @Before
    public void setUp() throws URISyntaxException {

        // create client to be used as the fk constraint.
        clientUUID = UUID.randomUUID();
        ResponseType rt = ResponseType.CODE;
        URI redirectURI = new URI("https://rootservices.org");
        Client client = new Client(clientUUID, rt, redirectURI);
        clientRepository.insert(client);

        // create auth user to be used as fk constraint
        resourceOwnerUUID = UUID.randomUUID();
        String email = "test@rootservices.com";
        byte[] password = "plainTextPassword".getBytes();
        ResourceOwner authUser =  new ResourceOwner(resourceOwnerUUID, email, password);
        resourceOwnerRepository.insert(authUser);
    }

    @Test
    @Transactional
    public void insert() {
        UUID uuid = UUID.randomUUID();
        byte [] code = "authortization_code".getBytes();
        OffsetDateTime expiresAt = OffsetDateTime.now();

        AuthCode authCode = new AuthCode(uuid, code, resourceOwnerUUID, clientUUID, expiresAt);
        subject.insert(authCode);
    }
}
