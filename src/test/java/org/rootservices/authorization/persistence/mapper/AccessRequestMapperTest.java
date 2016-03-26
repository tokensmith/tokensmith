package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.openid.LoadOpenIdConfidentialClientAll;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by tommackenzie on 4/15/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class AccessRequestMapperTest {

    @Autowired
    private LoadOpenIdConfidentialClientAll loadOpenIdConfidentialClientAll;

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
        Client client = FixtureFactory.makeClientWithScopes();
        clientRepository.insert(client);

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        resourceOwnerRepository.insert(resourceOwner);
        // end prepare db for test

        AccessRequest accessRequest = FixtureFactory.makeAccessRequest(
                resourceOwner,
                client.getUuid()
        );

        subject.insert(accessRequest);
    }

    @Test
    public void getByAccessToken() throws DuplicateRecordException, URISyntaxException {
        Token token = loadOpenIdConfidentialClientAll.run();
        AccessRequest actual = subject.getByAccessToken(token.getToken());

        assertThat(actual.getUuid(), is(notNullValue()));
        assertThat(actual.getRedirectURI().isPresent(), is(true));
        assertThat(actual.getCreatedAt(), is(notNullValue()));

        assertThat(actual.getResourceOwner(), is(notNullValue()));
        assertThat(actual.getResourceOwner().getUuid(), is(notNullValue()));
        assertThat(actual.getResourceOwner().getEmail(), is(notNullValue()));
        assertThat(actual.getResourceOwner().getPassword(), is(notNullValue()));
        assertThat(actual.getResourceOwner().isEmailVerified(), is(false));
        assertThat(actual.getResourceOwner().getCreatedAt(), is(notNullValue()));

        assertThat(actual.getAccessRequestScopes(), is(notNullValue()));
        assertThat(actual.getAccessRequestScopes().size(), is(1));
        assertThat(actual.getAccessRequestScopes().get(0).getUuid(), is(notNullValue()));
        assertThat(actual.getAccessRequestScopes().get(0).getCreatedAt(), is(notNullValue()));

        assertThat(actual.getAccessRequestScopes().get(0).getScope(), is(notNullValue()));
        assertThat(actual.getAccessRequestScopes().get(0).getScope().getUuid(), is(notNullValue()));
        assertThat(actual.getAccessRequestScopes().get(0).getScope().getName(), is(notNullValue()));
        assertThat(actual.getAccessRequestScopes().get(0).getScope().getName(), is("openid"));
        assertThat(actual.getAccessRequestScopes().get(0).getScope().getCreatedAt(), is(notNullValue()));


    }
}