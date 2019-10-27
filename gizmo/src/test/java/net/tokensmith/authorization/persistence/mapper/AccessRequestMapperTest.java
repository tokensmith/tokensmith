package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.TestAppConfig;
import helper.fixture.persistence.openid.LoadOpenIdConfClientAll;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.tokensmith.authorization.persistence.entity.*;
import net.tokensmith.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

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
    public void insert() throws Exception {
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
}