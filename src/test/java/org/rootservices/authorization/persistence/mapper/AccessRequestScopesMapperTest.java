package org.rootservices.authorization.persistence.mapper;

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
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/19/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class AccessRequestScopesMapperTest {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ResourceOwnerRepository resourceOwnerRepository;
    @Autowired
    private AuthCodeRepository authCodeRepository;
    @Autowired
    private AccessRequestRepository accessRequestRepository;
    @Autowired
    private ScopeRepository scopeRepository;

    @Autowired
    private AccessRequestScopesMapper subject;

    public AccessRequest persistAccessReqeust() throws URISyntaxException {
        // create client to be used as the fk constraint.
        UUID clientUUID = UUID.randomUUID();
        URI redirectURI = new URI("https://rootservices.org");
        Client client = new Client(clientUUID, redirectURI);
        clientRepository.insert(client);

        // create resource owner to be used as fk constraint
        UUID resourceOwnerId = UUID.randomUUID();
        String email = "test@rootservices.com";
        byte[] password = "plainTextPassword".getBytes();
        ResourceOwner ro =  new ResourceOwner(resourceOwnerId, email, password);
        resourceOwnerRepository.insert(ro);

        // finally, create the access reqeust.
        AccessRequest accessRequest = new AccessRequest(
                UUID.randomUUID(),
                ro.getUuid(),
                client.getUuid(),
                Optional.of(redirectURI)
        );
        accessRequestRepository.insert(accessRequest);

        return accessRequest;
    }

    public Scope persistScope() {
        Scope scope = new Scope(UUID.randomUUID(), "address");
        scopeRepository.insert(scope);
        return scope;
    }

    @Test
    public void insert() throws URISyntaxException {
        AccessRequest accessRequest = persistAccessReqeust();
        Scope scope = persistScope();

        AccessRequestScope accessRequestScope = new AccessRequestScope(
            UUID.randomUUID(), accessRequest.getUuid(), scope
        );

        subject.insert(accessRequestScope);
    }

}
