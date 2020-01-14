package net.toknsmith.login.translator;


import helper.Factory;
import net.toknsmith.login.HttpUtils;
import net.toknsmith.login.config.LoginFactory;
import net.toknsmith.login.exception.TranslateException;
import net.toknsmith.login.exception.http.api.ServerException;
import net.toknsmith.login.exception.http.openid.BadRequestException;
import net.toknsmith.login.exception.http.openid.ErrorResponseException;
import net.toknsmith.login.exception.http.openid.NotFoundException;
import net.toknsmith.login.exception.http.openid.ServerError;
import net.toknsmith.login.exception.http.openid.UnAuthorizedException;
import net.toknsmith.login.endpoint.entity.response.openid.TokenErrorResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.toknsmith.login.http.StatusCode;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class TokenErrorResponseExceptionFactoryTest {
    private ErrorResponseExceptionFactory subject;
    @Mock
    private ErrorResponseTranslator mockErrorResponseTranslator;

    private HttpUtils httpUtils;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new ErrorResponseExceptionFactory(mockErrorResponseTranslator);
        LoginFactory loginFactory = new LoginFactory();
        httpUtils = loginFactory.httpUtils();
    }

    @Test
    public void forTokenEndpointWhenTranslateExceptionShouldReturnErrorResponseException() throws Exception {
        HttpResponse<InputStream> fakeResponse = Factory.makeFakeResponseUnAuthorized();
        InputStream body = httpUtils.processResponse(fakeResponse);

        doThrow(TranslateException.class).when(mockErrorResponseTranslator).to(fakeResponse, body);

        ErrorResponseException actual = subject.forTokenEndpoint(fakeResponse, body);
        assertThat(actual, is(notNullValue()));
        assertThat(actual, instanceOf(ErrorResponseException.class));
        assertThat(actual.getErrorResponse().isPresent(), is(false));
        assertThat(actual.getStatusCode(), is(StatusCode.UNAUTHORIZED.getCode()));
    }

    @Test
    public void forTokenEndpointShouldThrowBadRequestException() throws Exception {
        HttpResponse<InputStream> fakeResponse = Factory.makeFakeResponseBadRequest();
        InputStream body = httpUtils.processResponse(fakeResponse);

        TokenErrorResponse er = new TokenErrorResponse("invalid_request", Optional.of("client_id is repeated"));
        when(mockErrorResponseTranslator.to(fakeResponse, body)).thenReturn(er);

        ErrorResponseException actual = subject.forTokenEndpoint(fakeResponse, body);
        assertThat(actual, is(notNullValue()));
        assertThat(actual, instanceOf(BadRequestException.class));
        assertThat(actual.getErrorResponse().isPresent(), is(true));
        assertThat(actual.getErrorResponse().get().getError(), is(er.getError()));
        assertThat(actual.getErrorResponse().get().getDescription(), is(er.getDescription()));
        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST.getCode()));
    }

    @Test
    public void forTokenEndpointShouldThrowUnAuthorizedException() throws Exception {
        HttpResponse<InputStream> fakeResponse = Factory.makeFakeResponseUnAuthorized();
        InputStream body = httpUtils.processResponse(fakeResponse);

        TokenErrorResponse er = new TokenErrorResponse("invalid_client", Optional.empty());
        when(mockErrorResponseTranslator.to(fakeResponse, body)).thenReturn(er);

        ErrorResponseException actual = subject.forTokenEndpoint(fakeResponse, body);
        assertThat(actual, is(notNullValue()));
        assertThat(actual, instanceOf(UnAuthorizedException.class));
        assertThat(actual.getErrorResponse().isPresent(), is(true));
        assertThat(actual.getErrorResponse().get().getError(), is(er.getError()));
        assertThat(actual.getErrorResponse().get().getDescription(), is(er.getDescription()));
        assertThat(actual.getStatusCode(), is(StatusCode.UNAUTHORIZED.getCode()));
    }

    @Test
    public void forTokenEndpointShouldThrowNotFoundException() throws Exception {
        HttpResponse<InputStream> fakeResponse = Factory.makeFakeResponseNotFound();
        InputStream body = httpUtils.processResponse(fakeResponse);

        TokenErrorResponse er = new TokenErrorResponse("invalid_grant", Optional.of("the authorization code was already used"));
        when(mockErrorResponseTranslator.to(fakeResponse, body)).thenReturn(er);

        ErrorResponseException actual = subject.forTokenEndpoint(fakeResponse, body);
        assertThat(actual, is(notNullValue()));
        assertThat(actual, instanceOf(NotFoundException.class));
        assertThat(actual.getErrorResponse().isPresent(), is(true));
        assertThat(actual.getErrorResponse().get().getError(), is(er.getError()));
        assertThat(actual.getErrorResponse().get().getDescription(), is(er.getDescription()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_FOUND.getCode()));
    }

    @Test
    public void forTokenEndpointShouldThrowServerErrorException() throws Exception {
        HttpResponse<InputStream> fakeResponse = Factory.makeFakeResponseServerError();
        InputStream body = httpUtils.processResponse(fakeResponse);

        TokenErrorResponse er = new TokenErrorResponse("Unhandled Server Exception", Optional.of("Unhandled Server Exception"));
        when(mockErrorResponseTranslator.to(fakeResponse, body)).thenReturn(er);

        ErrorResponseException actual = subject.forTokenEndpoint(fakeResponse, body);
        assertThat(actual, is(notNullValue()));
        assertThat(actual, instanceOf(ServerError.class));
        assertThat(actual.getErrorResponse().isPresent(), is(true));
        assertThat(actual.getErrorResponse().get().getError(), is(er.getError()));
        assertThat(actual.getErrorResponse().get().getDescription(), is(er.getDescription()));
        assertThat(actual.getStatusCode(), is(StatusCode.SERVER_ERROR.getCode()));
    }

    @Test
    public void forTokenEndpointWhenTooManyShouldThrowErrorException() throws Exception {
        HttpResponse<InputStream> fakeResponse = Factory.makeFakeResponseTooMany();
        InputStream body = httpUtils.processResponse(fakeResponse);

        TokenErrorResponse er = new TokenErrorResponse("Too Many", Optional.of("Gear down big shifter"));
        when(mockErrorResponseTranslator.to(fakeResponse, body)).thenReturn(er);

        ErrorResponseException actual = subject.forTokenEndpoint(fakeResponse, body);
        assertThat(actual, is(notNullValue()));
        assertThat(actual, instanceOf(ErrorResponseException.class));
        assertThat(actual.getErrorResponse().isPresent(), is(true));
        assertThat(actual.getErrorResponse().get().getError(), is(er.getError()));
        assertThat(actual.getErrorResponse().get().getDescription(), is(er.getDescription()));
        assertThat(actual.getStatusCode(), is(StatusCode.TOO_MANY.getCode()));
    }

    @Test
    public void forUserEndpointShouldThrowBadRequestException() throws Exception {

        ErrorResponseException actual = subject.forUserEndpoint(StatusCode.BAD_REQUEST.getCode());
        assertThat(actual, is(notNullValue()));
        assertThat(actual, instanceOf(BadRequestException.class));
        assertThat(actual.getErrorResponse().isPresent(), is(false));
        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST.getCode()));
    }

    @Test
    public void forUserEndpointShouldThrowUnAuthorizedException() throws Exception {

        ErrorResponseException actual = subject.forUserEndpoint(StatusCode.UNAUTHORIZED.getCode());
        assertThat(actual, is(notNullValue()));
        assertThat(actual, instanceOf(UnAuthorizedException.class));
        assertThat(actual.getErrorResponse().isPresent(), is(false));
        assertThat(actual.getStatusCode(), is(StatusCode.UNAUTHORIZED.getCode()));
    }

    @Test
    public void forUserEndpointShouldThrowNotFoundException() throws Exception {

        ErrorResponseException actual = subject.forUserEndpoint(StatusCode.NOT_FOUND.getCode());
        assertThat(actual, is(notNullValue()));
        assertThat(actual, instanceOf(NotFoundException.class));
        assertThat(actual.getErrorResponse().isPresent(), is(false));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_FOUND.getCode()));
    }

    @Test
    public void forUserEndpointShouldThrowErrorResponseException() throws Exception {

        ErrorResponseException actual = subject.forUserEndpoint(StatusCode.TOO_MANY.getCode());
        assertThat(actual, is(notNullValue()));
        assertThat(actual, instanceOf(ErrorResponseException.class));
        assertThat(actual.getErrorResponse().isPresent(), is(false));
        assertThat(actual.getStatusCode(), is(StatusCode.TOO_MANY.getCode()));
    }

    @Test
    public void forUserEndpointShouldThrowServerError() throws Exception {

        ErrorResponseException actual = subject.forUserEndpoint(StatusCode.SERVER_ERROR.getCode());
        assertThat(actual, is(notNullValue()));
        assertThat(actual, instanceOf(ServerError.class));
        assertThat(actual.getErrorResponse().isPresent(), is(false));
        assertThat(actual.getStatusCode(), is(StatusCode.SERVER_ERROR.getCode()));
    }
}