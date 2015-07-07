package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadConfidentialClientTokenReady;
import helper.fixture.persistence.LoadClientWithScopes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 5/23/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class TokenMapperTest {

    @Autowired
    private LoadConfidentialClientTokenReady loadConfidentialClientTokenReady;

    @Autowired
    private TokenMapper subject;

    @Test
    public void insert() throws URISyntaxException {
        AuthCode authCode = loadConfidentialClientTokenReady.run();
        Token token = FixtureFactory.makeToken(authCode.getUuid());
        subject.insert(token);
    }
}
