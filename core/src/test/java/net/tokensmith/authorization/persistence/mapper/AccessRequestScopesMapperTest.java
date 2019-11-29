package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.TestAppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.tokensmith.repository.entity.*;
import net.tokensmith.repository.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/19/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= TestAppConfig.class, loader= AnnotationConfigContextLoader.class)
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

    public AccessRequest persistAccessReqeust() throws Exception {
        // create client to be used as the fk constraint.
        UUID clientUUID = UUID.randomUUID();
        URI redirectURI = new URI("https://tokensmith.net");
        Client client = new Client(clientUUID, redirectURI);
        clientRepository.insert(client);

        // create resource owner to be used as fk constraint
        UUID resourceOwnerId = UUID.randomUUID();
        String email = "test@rootservices.com";
        String password = "plainTextPassword";
        ResourceOwner ro =  new ResourceOwner(resourceOwnerId, email, password);
        resourceOwnerRepository.insert(ro);

        // finally, create the access reqeust.
        AccessRequest accessRequest = new AccessRequest(
                UUID.randomUUID(),
                ro.getId(),
                client.getId(),
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
    public void insert() throws Exception {
        AccessRequest accessRequest = persistAccessReqeust();
        Scope scope = persistScope();

        AccessRequestScope accessRequestScope = new AccessRequestScope(
            UUID.randomUUID(), accessRequest.getId(), scope
        );

        subject.insert(accessRequestScope);
    }

}
