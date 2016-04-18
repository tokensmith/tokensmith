package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadConfidentialClientTokenReady;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.entity.TokenScope;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 4/17/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class TokenScopeMapperTest {

    @Autowired
    private LoadConfidentialClientTokenReady loadConfidentialClientTokenReady;
    @Autowired
    private RandomString randomString;
    @Autowired
    private TokenMapper tokenMapper;
    @Autowired
    private TokenScopeMapper subject;

    @Test
    public void insertShouldBeOk() throws Exception {
        // begin prepare db for test
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);
        Token token = FixtureFactory.makeToken(authCode.getUuid());
        tokenMapper.insert(token);

        Scope scope = authCode.getAccessRequest().getAccessRequestScopes().get(0).getScope();
        // end prepare db for test.

        TokenScope tokenScope = new TokenScope();
        tokenScope.setId(UUID.randomUUID());
        tokenScope.setTokenId(token.getUuid());
        tokenScope.setScope(scope);

        subject.insert(tokenScope);
    }


}