package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.TokenAudience;
import org.rootservices.authorization.persistence.entity.GrantType;
import org.rootservices.authorization.persistence.entity.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 10/2/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class TokenAudienceMapperTest {
    @Autowired
    private TokenAudienceMapper subject;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private TokenMapper tokenMapper;

    public UUID loadClient() throws Exception {
        Client client = FixtureFactory.makeTokenClientWithScopes();
        clientMapper.insert(client);
        return client.getId();
    }

    public UUID loadToken(String accessToken, UUID clientId) throws Exception {
        Token token = FixtureFactory.makeOAuthToken(accessToken, clientId, new ArrayList<>());
        token.setGrantType(GrantType.TOKEN);
        tokenMapper.insert(token);
        return token.getId();
    }

    @Test
    public void insertShouldBeOk() throws Exception {
        String accessToken = "access-token";
        UUID clientId = loadClient();
        UUID tokenId = loadToken(accessToken, clientId);

        TokenAudience clientToken = new TokenAudience();
        clientToken.setId(UUID.randomUUID());
        clientToken.setClientId(clientId);
        clientToken.setTokenId(tokenId);

        subject.insert(clientToken);

        TokenAudience actual = subject.getByTokenId(tokenId);
        assertThat(actual.getId(), is(clientToken.getId()));
        assertThat(actual.getClientId(), is(clientId));
        assertThat(actual.getTokenId(), is(tokenId));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
    }
}