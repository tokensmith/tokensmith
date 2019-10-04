package org.rootservices.authorization.oauth2.grant.token;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.exception.BadRequestException;
import org.rootservices.authorization.oauth2.grant.token.factory.RequestTokenGrantFactory;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class RequestTokenTest {
    private RequestToken subject;
    @Mock
    private RequestTokenGrantFactory mockRequestTokenGrantFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new RequestToken(mockRequestTokenGrantFactory);
    }

    @Test
    public void requestShouldBeOk() throws Exception {
        UUID clientId = UUID.randomUUID();
        String clientUserName = clientId.toString();
        String clientPassword = "password";

        Map<String, String> tokenRequest = new HashMap<>();
        tokenRequest.put("grant_type", "password");
        RequestTokenGrant mockRequestTokenGrant = mock(RequestTokenGrant.class);
        TokenResponse response = new TokenResponse();

        when(mockRequestTokenGrantFactory.make("password")).thenReturn(mockRequestTokenGrant);
        when(mockRequestTokenGrant.request(clientId, clientPassword, tokenRequest)).thenReturn(response);

        TokenResponse actual = subject.request(clientUserName, clientPassword, tokenRequest);
        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(response));
    }

    @Test
    public void requestWhenUserNameIsNotUUIDShouldThrowUnauthorizedException() throws Exception {
        String clientUserName = "foo";
        String clientPassword = "password";

        Map<String, String> tokenRequest = new HashMap<>();
        tokenRequest.put("grant_type", "password");

        UnauthorizedException actual = null;
        try {
            subject.request(clientUserName, clientPassword, tokenRequest);
        } catch(UnauthorizedException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is(ErrorCode.CLIENT_USERNAME_DATA_TYPE.getDescription()));
        assertThat(actual.getCode(), is(ErrorCode.CLIENT_USERNAME_DATA_TYPE.getCode()));
        assertThat(actual.getCause(), instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void requestWhenGrantTypeInvalidShouldThrowBadRequestException() throws Exception {
        String clientUserName = UUID.randomUUID().toString();
        String clientPassword = "password";

        Map<String, String> tokenRequest = new HashMap<>();
        tokenRequest.put("grant_type", "unknown");

        when(mockRequestTokenGrantFactory.make("unknown")).thenReturn(null);

        BadRequestException actual = null;

        try {
            subject.request(clientUserName, clientPassword, tokenRequest);
        } catch (BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getCode(), is(ErrorCode.GRANT_TYPE_INVALID.getCode()));
        assertThat(actual.getMessage(), is("Bad request"));
        assertThat(actual.getError(), is("invalid_request"));
        assertThat(actual.getDescription(), is("grant_type is invalid"));
    }
}