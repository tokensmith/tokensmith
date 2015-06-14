package org.rootservices.authorization.persistence.mapper;

import helper.fixture.persistence.LoadClientWithScopes;
import helper.fixture.persistence.LoadConfidentialClientTokenReady;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.grant.code.request.AuthRequest;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 4/15/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class AccessRequestMapperTest {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ConfidentialClientRepository confidentialClientRepository;
    @Autowired
    private ScopeRepository scopeRepository;
    @Autowired
    private ClientScopesRepository clientScopesRepository;
    @Autowired
    private ResourceOwnerRepository resourceOwnerRepository;
    @Autowired
    private AuthCodeRepository authCodeRepository;
    @Autowired
    private AccessRequestRepository accessRequestRepository;
    @Autowired
    private AccessRequestScopesRepository accessRequestScopesRepository;

    @Autowired
    private AccessRequestMapper subject;


    /**
     * Creates foreign key relationships and assigns static variables
     * used in tests.
     *
     * @throws URISyntaxException
     */
    public AuthCode prepareDataBaseForAccessRequestInsert(URI redirectURI) throws URISyntaxException {

        // create client to be used as the fk constraint.
        ResponseType responseType = ResponseType.CODE;
        Client client = new Client(UUID.randomUUID(), responseType, redirectURI);
        clientRepository.insert(client);

        // create auth user to be used as fk constraint
        String email = "test@rootservices.com";
        byte[] password = "plainTextPassword".getBytes();
        ResourceOwner ro =  new ResourceOwner(UUID.randomUUID(), email, password);
        resourceOwnerRepository.insert(ro);

        // create auth code to be used as fk constraint
        byte [] code = "authortization_code".getBytes();
        OffsetDateTime expiresAt = OffsetDateTime.now();

        AuthCode authCode = new AuthCode(UUID.randomUUID(), code, ro.getUuid(), client.getUuid(), expiresAt);
        authCodeRepository.insert(authCode);

        return authCode;
    }

    @Test
    public void insert() throws Exception {
        URI redirectURI = new URI("https://rootservices.org");
        AuthCode authCode = prepareDataBaseForAccessRequestInsert(redirectURI);
        AccessRequest accessRequest = new AccessRequest(
                UUID.randomUUID(),
                authCode.getResourceOwnerUUID(),
                authCode.getClientUUID(),
                Optional.of(redirectURI),
                authCode.getUuid()
        );

        subject.insert(accessRequest);
    }


    @Test
    public void getByClientUUIDAndAuthCode() throws URISyntaxException {
        LoadClientWithScopes loadClientWithScopes = new LoadClientWithScopes(
                clientRepository,
                scopeRepository,
                clientScopesRepository
        );

        LoadConfidentialClientTokenReady loadConfidentialClientTokenReady = new LoadConfidentialClientTokenReady(
                loadClientWithScopes,
                confidentialClientRepository,
                resourceOwnerRepository,
                authCodeRepository,
                accessRequestRepository,
                accessRequestScopesRepository
        );

        AuthCode authCode = loadConfidentialClientTokenReady.run();
        AccessRequest ar = subject.getByClientUUIDAndAuthCode(
                authCode.getClientUUID(), "authortization_code");

        assertThat(ar).isNotNull();
        assertThat(ar.getRedirectURI().isPresent()).isTrue();
        assertThat(ar.getRedirectURI().get()).isEqualTo(new URI("https://rootservices.org"));

        assertThat(ar.getScopes()).isNotNull();
        assertThat(ar.getScopes().size()).isEqualTo(1);
        assertThat(ar.getScopes().get(0).getName()).isEqualTo("profile");
    }
}