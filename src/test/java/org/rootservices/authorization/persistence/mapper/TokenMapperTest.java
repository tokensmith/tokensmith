package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadClientWithScopes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.grant.code.authenticate.GrantAuthCode;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by tommackenzie on 5/23/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class TokenMapperTest {

    private LoadClientWithScopes loadClientWithScopes;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ScopeRepository scopeRepository;

    @Autowired
    private ClientScopesRepository clientScopesRepository;

    @Autowired
    private ResourceOwnerRepository resourceOwnerRepository;

    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private AuthCodeRepository authCodeRepository;

    @Autowired
    private TokenMapper subject;

    @Before
    public void setUp() {
        loadClientWithScopes = new LoadClientWithScopes(clientRepository, scopeRepository, clientScopesRepository);
    }

    private AuthCode setUpDatabase() throws URISyntaxException {
        Client client = loadClientWithScopes.run();
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerRepository.insert(ro);
        AuthCode authCode = FixtureFactory.makeAuthCode(ro.getUuid(), client.getUuid());
        authCodeRepository.insert(authCode);
        AccessRequest accessRequest = FixtureFactory.makeAccessRequest(authCode.getUuid());
        accessRequestRepository.insert(accessRequest);

        return authCode;
    }

    @Test
    public void insert() throws URISyntaxException {
        AuthCode authCode = setUpDatabase();
        Token token = FixtureFactory.makeToken(authCode.getUuid());
        subject.insert(token);
    }
}
