package org.rootservices.authorization.persistence.mapper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/15/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
public class AuthRequestMapperTest {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ResourceOwnerRepository resourceOwnerRepository;
    @Autowired
    private AuthCodeRepository authCodeRepository;

    @Autowired
    private AuthRequestMapper subject;

    // assigned in setUp()
    private UUID clientUUID;
    private UUID resourceOwnerUUID;
    private UUID authCodeUUID;
    private ResponseType responseType;
    private URI redirectURI;

    /**
     * Creates foreign key relationships and assigns static variables
     * used in tests.
     *
     * @throws URISyntaxException
     */
    @Before
    public void setUp() throws URISyntaxException {

        // create client to be used as the fk constraint.
        clientUUID = UUID.randomUUID();
        responseType = ResponseType.CODE;
        redirectURI = new URI("https://rootservices.org");
        Client client = new Client(clientUUID, responseType, redirectURI);
        clientRepository.insert(client);

        // create auth user to be used as fk constraint
        resourceOwnerUUID = UUID.randomUUID();
        String email = "test@rootservices.com";
        byte[] password = "plainTextPassword".getBytes();
        ResourceOwner authUser =  new ResourceOwner(resourceOwnerUUID, email, password);
        resourceOwnerRepository.insert(authUser);

        // create auth code to be used as fk constraint
        authCodeUUID = UUID.randomUUID();
        byte [] code = "authortization_code".getBytes();
        Calendar expiresAt = Calendar.getInstance();

        AuthCode authCode = new AuthCode(authCodeUUID, code, resourceOwnerUUID, clientUUID, expiresAt.getTime());
        authCodeRepository.insert(authCode);
    }

    @Test
    @Transactional
    public void insert() throws Exception {
        UUID uuid = UUID.randomUUID();
        AuthRequest authRequest = new AuthRequest(
                uuid, responseType, Optional.of(redirectURI), authCodeUUID
        );
        subject.insert(authRequest);
    }
}