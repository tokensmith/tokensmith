package org.rootservices.authorization.oauth2.grant.token;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.oauth2.grant.token.exception.BadRequestException;
import org.rootservices.authorization.oauth2.grant.token.exception.BadRequestExceptionBuilder;
import org.rootservices.authorization.oauth2.grant.token.exception.DuplicateKeyException;
import org.rootservices.authorization.oauth2.grant.token.exception.InvalidPayloadException;
import org.rootservices.authorization.oauth2.grant.token.factory.RequestTokenGrantFactory;
import org.rootservices.authorization.oauth2.grant.token.translator.JsonToMapTranslator;

import java.io.BufferedReader;
import java.io.StringReader;
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

/**
 * Created by tommackenzie on 9/27/16.
 */
public class RequestTokenTest {
    private RequestToken subject;
    @Mock
    private JsonToMapTranslator mockJsonToMapTranslator;
    @Mock
    private RequestTokenGrantFactory mockRequestTokenGrantFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new RequestToken(
                mockJsonToMapTranslator,
                new BadRequestExceptionBuilder(),
                mockRequestTokenGrantFactory
        );
    }

    @Test
    public void requestShouldBeOk() throws Exception {
        UUID clientId = UUID.randomUUID();
        String clientUserName = clientId.toString();
        String clientPassword = "password";
        StringReader src = new StringReader("foo");
        BufferedReader request = new BufferedReader(src);

        Map<String, String> tokenInput = new HashMap<>();
        tokenInput.put("grant_type", "password");
        RequestTokenGrant mockRequestTokenGrant = mock(RequestTokenGrant.class);
        TokenResponse response = new TokenResponse();

        when(mockJsonToMapTranslator.to(request)).thenReturn(tokenInput);
        when(mockRequestTokenGrantFactory.make("password")).thenReturn(mockRequestTokenGrant);
        when(mockRequestTokenGrant.request(clientId, clientPassword, tokenInput)).thenReturn(response);

        TokenResponse actual = subject.request(clientUserName, clientPassword, request);
        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(response));
    }

    @Test
    public void requestWhenUserNameIsNotUUIDShouldThrowUnauthorizedException() throws Exception {
        String clientUserName = "foo";
        String clientPassword = "password";
        StringReader src = new StringReader("foo");
        BufferedReader request = new BufferedReader(src);

        Map<String, String> tokenInput = new HashMap<>();
        tokenInput.put("grant_type", "password");

        UnauthorizedException actual = null;
        try {
            subject.request(clientUserName, clientPassword, request);
        } catch(UnauthorizedException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is(ErrorCode.CLIENT_USERNAME_DATA_TYPE.getDescription()));
        assertThat(actual.getCode(), is(ErrorCode.CLIENT_USERNAME_DATA_TYPE.getCode()));
        assertThat(actual.getDomainCause(), instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void requestWhenDuplicateKeyShouldThrowBadRequestException() throws Exception {
        String clientUserName = UUID.randomUUID().toString();
        String clientPassword = "password";
        StringReader src = new StringReader("foo");
        BufferedReader request = new BufferedReader(src);

        DuplicateKeyException dke = new DuplicateKeyException(
                ErrorCode.DUPLICATE_KEY.getDescription(),
                null,
                ErrorCode.DUPLICATE_KEY.getCode(),
                "foo"
        );

        when(mockJsonToMapTranslator.to(request)).thenThrow(dke);

        BadRequestException actual = null;

        try {
            subject.request(clientUserName, clientPassword, request);
        } catch (BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getDomainCause(), is(dke));
        assertThat(actual.getCode(), is(dke.getCode()));
        assertThat(actual.getMessage(), is("Bad request"));
        assertThat(actual.getError(), is("invalid_request"));
        assertThat(actual.getDescription(), is("foo is repeated"));
    }
    @Test
    public void requestWhenInvalidPayloadShouldThrowBadRequestException() throws Exception {
        String clientUserName = UUID.randomUUID().toString();
        String clientPassword = "password";
        StringReader src = new StringReader("foo");
        BufferedReader request = new BufferedReader(src);

        InvalidPayloadException ipe = new InvalidPayloadException(
                ErrorCode.INVALID_PAYLOAD.getDescription(),
                null,
                ErrorCode.INVALID_PAYLOAD.getCode()
        );

        when(mockJsonToMapTranslator.to(request)).thenThrow(ipe);

        BadRequestException actual = null;

        try {
            subject.request(clientUserName, clientPassword, request);
        } catch (BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getDomainCause(), is(ipe));
        assertThat(actual.getCode(), is(ipe.getCode()));
        assertThat(actual.getMessage(), is("Bad request"));
        assertThat(actual.getError(), is("invalid_request"));
        assertThat(actual.getDescription(), is("payload is not json"));
    }

    @Test
    public void requestWhenGrantTypeInvalidShouldThrowBadRequestException() throws Exception {
        String clientUserName = UUID.randomUUID().toString();
        String clientPassword = "password";
        StringReader src = new StringReader("foo");
        BufferedReader request = new BufferedReader(src);

        Map<String, String> tokenInput = new HashMap<>();
        tokenInput.put("grant_type", "unknown");

        when(mockJsonToMapTranslator.to(request)).thenReturn(tokenInput);
        when(mockRequestTokenGrantFactory.make("unknown")).thenReturn(null);

        BadRequestException actual = null;

        try {
            subject.request(clientUserName, clientPassword, request);
        } catch (BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getDomainCause(), is(nullValue()));
        assertThat(actual.getCode(), is(ErrorCode.GRANT_TYPE_INVALID.getCode()));
        assertThat(actual.getMessage(), is("Bad request"));
        assertThat(actual.getError(), is("invalid_request"));
        assertThat(actual.getDescription(), is("grant_type is invalid"));

    }
}