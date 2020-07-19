package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.TestAppConfig;
import helper.fixture.persistence.openid.LoadOpenIdConfClientAll;
import net.tokensmith.repository.entity.AccessRequest;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.repo.AccessRequestRepository;
import net.tokensmith.repository.repo.ClientRepository;
import net.tokensmith.repository.repo.ResourceOwnerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by tommackenzie on 4/15/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= TestAppConfig.class, loader= AnnotationConfigContextLoader.class)
@Transactional
public class AccessRequestMapperTest {

    @Autowired
    private LoadOpenIdConfClientAll loadOpenIdConfClientAll;

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ResourceOwnerRepository resourceOwnerRepository;
    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private AccessRequestMapper subject;

    @Test
    public void insertWhenNonceIsEmpty() throws Exception {
        // prepare db for test.
        Client client = FixtureFactory.makeCodeClientWithScopes();
        clientRepository.insert(client);

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        resourceOwnerRepository.insert(resourceOwner);
        // end prepare db for test

        AccessRequest accessRequest = FixtureFactory.makeAccessRequest(
                resourceOwner.getId(),
                client.getId()
        );

        subject.insert(accessRequest);
    }

    @Test
    public void insertWhenNonceHasValue() throws Exception {
        // prepare db for test.
        Client client = FixtureFactory.makeCodeClientWithScopes();
        clientRepository.insert(client);

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        resourceOwnerRepository.insert(resourceOwner);
        // end prepare db for test

        AccessRequest accessRequest = FixtureFactory.makeAccessRequest(
                resourceOwner.getId(),
                client.getId()
        );
        accessRequest.setNonce(Optional.of("nonce-123"));

        subject.insert(accessRequest);
    }
}