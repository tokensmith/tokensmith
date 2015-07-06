package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadClientWithScopes;
import helper.fixture.persistence.LoadConfidentialClientTokenReady;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 4/10/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class AuthCodeMapperTest {

    @Autowired
    private LoadConfidentialClientTokenReady loadConfidentialClientTokenReady;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ResourceOwnerRepository resourceOwnerRepository;
    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private AuthCodeMapper subject;

    @Test
    public void insert() throws URISyntaxException {

        // prepare db for test.
        Client client = FixtureFactory.makeClientWithScopes();
        clientRepository.insert(client);

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        resourceOwnerRepository.insert(resourceOwner);

        AccessRequest accessRequest = FixtureFactory.makeAccessRequest(
                resourceOwner.getUuid(),
                client.getUuid()
        );
        accessRequestRepository.insert(accessRequest);
        // end prepare db for test.

        AuthCode authCode = FixtureFactory.makeAuthCode(accessRequest);
        subject.insert(authCode);
    }

    @Test
    public void getByClientUUIDAndAuthCode() throws URISyntaxException {

        AuthCode expected = loadConfidentialClientTokenReady.run();

        AuthCode actual = subject.getByClientUUIDAndAuthCode(expected.getAccessRequest().getClientUUID(), "authortization_code");

        assertThat(actual).isNotNull();
        assertThat(actual.getUuid()).isEqualTo(expected.getUuid());
        assertThat(actual.getAccessRequest()).isNotNull();
        assertThat(actual.getAccessRequest().getUuid()).isEqualTo(expected.getAccessRequest().getUuid());
        assertThat(actual.getAccessRequest().getScopes()).isNotNull();
        assertThat(actual.getAccessRequest().getScopes().size()).isEqualTo(1);
        assertThat(actual.getAccessRequest().getScopes().get(0).getName()).isEqualTo("profile");
    }
}
