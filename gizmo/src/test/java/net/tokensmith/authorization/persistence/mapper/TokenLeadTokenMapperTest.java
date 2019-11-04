package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.TestAppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.Token;
import net.tokensmith.repository.entity.TokenLeadToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 12/1/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= TestAppConfig.class, loader= AnnotationConfigContextLoader.class)
@Transactional
public class TokenLeadTokenMapperTest {
    @Autowired
    private TokenLeadTokenMapper subject;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private TokenMapper tokenMapper;

    public Token loadToken(String accessToken, UUID clientId) {
        Token token = FixtureFactory.makeOAuthToken(accessToken, clientId, new ArrayList<>());
        tokenMapper.insert(token);
        return token;
    }

    public Client loadClient() throws Exception {
        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        clientMapper.insert(client);
        return client;
    }

    public TokenLeadToken loadTokenLeadToken() throws Exception{
        Client client = loadClient();
        Token token = loadToken("access-token", client.getId());
        Token leadToken = loadToken("lead-access-token", client.getId());

        UUID id = UUID.randomUUID();
        TokenLeadToken tokenLeadToken = new TokenLeadToken();
        tokenLeadToken.setId(id);
        tokenLeadToken.setTokenId(token.getId());
        tokenLeadToken.setLeadTokenId(leadToken.getId());

        subject.insert(tokenLeadToken);

        return tokenLeadToken;
    }

    @Test
    public void getByIdShouldBeOk() throws Exception {
        TokenLeadToken tokenLeadToken = loadTokenLeadToken();

        TokenLeadToken actual = subject.getById(tokenLeadToken.getId());
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(tokenLeadToken.getId()));
        assertThat(actual.getTokenId(), is(tokenLeadToken.getTokenId()));
        assertThat(actual.getLeadTokenId(), is(tokenLeadToken.getLeadTokenId()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
    }

    @Test
    public void insertShouldBeOk() throws Exception {
        Client client = loadClient();
        Token token = loadToken("access-token", client.getId());
        Token leadToken = loadToken("lead-access-token", client.getId());

        UUID id = UUID.randomUUID();
        TokenLeadToken tokenLeadToken = new TokenLeadToken();
        tokenLeadToken.setId(id);
        tokenLeadToken.setTokenId(token.getId());
        tokenLeadToken.setLeadTokenId(leadToken.getId());

        subject.insert(tokenLeadToken);
    }
}