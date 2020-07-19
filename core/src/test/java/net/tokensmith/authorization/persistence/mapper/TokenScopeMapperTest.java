package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.TestAppConfig;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.Scope;
import net.tokensmith.repository.entity.Token;
import net.tokensmith.repository.entity.TokenScope;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/17/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= TestAppConfig.class, loader= AnnotationConfigContextLoader.class)
@Transactional
public class TokenScopeMapperTest {

    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private TokenMapper tokenMapper;
    @Autowired
    private ScopeMapper scopeMapper;
    @Autowired
    private TokenScopeMapper subject;

    @Test
    public void insertShouldBeOk() throws Exception {
        // begin prepare db for test
        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        clientMapper.insert(client);

        String accessToken = "access-token";
        Token token = FixtureFactory.makeOpenIdToken(accessToken, client.getId(), new ArrayList<>());
        tokenMapper.insert(token);

        Scope scope = FixtureFactory.makeScope();
        scope.setName("address");
        scopeMapper.insert(scope);

        // end prepare db for test.

        TokenScope tokenScope = new TokenScope();
        tokenScope.setId(UUID.randomUUID());
        tokenScope.setTokenId(token.getId());
        tokenScope.setScope(scope);

        subject.insert(tokenScope);
    }


}