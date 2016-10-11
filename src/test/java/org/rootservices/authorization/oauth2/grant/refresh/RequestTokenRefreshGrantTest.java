package org.rootservices.authorization.oauth2.grant.refresh;

import helper.fixture.persistence.openid.LoadOpenIdConfClientAll;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.entity.RefreshToken;
import org.rootservices.authorization.persistence.entity.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 10/10/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-auth-test.xml")
@Transactional
public class RequestTokenRefreshGrantTest {
    @Autowired
    private RequestTokenRefreshGrant subject;
    @Autowired
    private LoadOpenIdConfClientAll loadOpenIdConfClientAll;

    @Test
    public void requestShouldBeOk() throws Exception {
        AuthCode authCode = loadOpenIdConfClientAll.loadAuthCode();
        UUID clientId = authCode.getAccessRequest().getClientId();
        UUID resourceOwnerId = authCode.getAccessRequest().getResourceOwnerId();

        List<Scope> scopesForToken = authCode.getAccessRequest().getAccessRequestScopes().stream()
                .map(item -> item.getScope())
                .collect(Collectors.toList());

        OffsetDateTime tokenExpirationAt = OffsetDateTime.now().minusDays(1);
        RefreshToken refreshToken = loadOpenIdConfClientAll.loadRefreshToken(
                tokenExpirationAt, authCode.getId(), clientId, resourceOwnerId, scopesForToken
        );

        Map<String, String> request = new HashMap<>();
        request.put("grant_type", "refresh_token");
        request.put("refresh_token", new String(refreshToken.getAccessToken()));

        TokenResponse actual = subject.request(clientId, "password", request);
        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void requestWhenMissingKeyShould() {

    }

    @Test
    public void requestWhenInvalidValueShould() {

    }

    @Test
    public void requestWhenUnknownKeyShould() {

    }

    @Test
    public void requestWhenRefreshTokenNotFoundShould() {

    }

    @Test
    public void requestWhenExtraScopesShould() {

    }

    @Test
    public void requestRefreshTokenDoesNotBelongToClientShould() {

    }

    @Test
    public void requestWhenResourceOwnerNotLinkedToRefreshTokenShould() {

    }

    @Test
    public void requestWhenCompromisedRefreshTokenShould() {

    }
}