package net.toknsmith.login.endpoint;


import com.fasterxml.jackson.databind.ObjectReader;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.tokensmith.jwt.exception.InvalidJWT;
import net.tokensmith.jwt.serialization.JwtSerde;
import net.tokensmith.jwt.serialization.exception.JsonToJwtException;
import net.toknsmith.login.HttpUtils;
import net.toknsmith.login.config.props.EndpointProps;
import net.toknsmith.login.constant.Delimitter;
import net.toknsmith.login.exception.CommException;
import net.toknsmith.login.exception.TranslateException;
import net.toknsmith.login.exception.http.openid.ErrorResponseException;
import net.toknsmith.login.endpoint.entity.response.openid.OpenIdToken;
import net.toknsmith.login.endpoint.entity.response.openid.claim.User;
import net.toknsmith.login.http.ContentType;
import net.toknsmith.login.http.Header;
import net.toknsmith.login.http.HeaderValue;
import net.toknsmith.login.http.StatusCode;
import net.toknsmith.login.endpoint.entity.request.FormFields;
import net.toknsmith.login.endpoint.entity.request.GrantType;
import net.toknsmith.login.translator.ErrorResponseExceptionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;


public class UserEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEndpoint.class);
    private static String COMM_MSG = "Failed to communicate with Identity Server";
    public static String ERROR_MSG = "Unable to serialize the response from the Identity Server";
    private static String JWT_MSG = "Unable to serialize the id token";
    private static String BEARER_AUTH = "Bearer %s";
    private HttpClient httpClient;
    private HttpUtils httpUtils;
    private EndpointProps endpointProps;
    private ErrorResponseExceptionFactory errorResponseExceptionFactory;
    private ObjectReader openIdTokenReader;
    private JwtAppFactory jwtAppFactory;

    public UserEndpoint(HttpClient httpClient, HttpUtils httpUtils, EndpointProps endpointProps, ErrorResponseExceptionFactory errorResponseExceptionFactory, ObjectReader openIdTokenReader, JwtAppFactory jwtAppFactory) {
        this.httpClient = httpClient;
        this.httpUtils = httpUtils;
        this.endpointProps = endpointProps;
        this.errorResponseExceptionFactory = errorResponseExceptionFactory;
        this.openIdTokenReader = openIdTokenReader;
        this.jwtAppFactory = jwtAppFactory;
    }

    public OpenIdToken postTokenPasswordGrant(String username, String password, List<String> scopes) throws CommException, TranslateException, ErrorResponseException {
        Map<String, List<String>> form  = makePasswordForm(username, password, scopes);
        return postToken(form);
    }

    public OpenIdToken postTokenRefreshGrant(String refreshToken) throws TranslateException, ErrorResponseException, CommException {
        Map<String, List<String>> form = makeRefreshForm(refreshToken);
        return postToken(form);
    }

    public OpenIdToken postTokenCodeGrant(String code, String redirectUri) throws TranslateException, ErrorResponseException, CommException {
        Map<String, List<String>> form = makeCodeForm(code, redirectUri);
        return postToken(form);
    }

    public OpenIdToken postToken(Map<String, List<String>> form) throws CommException, TranslateException, ErrorResponseException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(endpointProps.getTokenEndpoint())
                .timeout(Duration.ofSeconds(2))
                .header(Header.CONTENT_TYPE.toString(), ContentType.FORM_URL_ENCODED.toString())
                .header(Header.AUTHORIZATION.toString(), endpointProps.getClientCredentials())
                .header(Header.ACCEPT.toString(), ContentType.JSON_UTF_8.toString())
                .header(Header.ACCEPT_ENCODING.toString(), "gzip")
                .header(Header.CORRELATION_ID.toString(), httpUtils.getCorrelationId())
                .header(Header.LOGIN_SDK.toString(), HeaderValue.LOGIN_SDK.toString())
                .header(Header.LOGIN_SDK_VERSION.toString(), HeaderValue.LOGIN_SDK_VERSION.toString())
                .POST(HttpRequest.BodyPublishers.ofString(httpUtils.toBody(form)))
                .version(HttpClient.Version.HTTP_2)
                .build();

        HttpResponse<InputStream> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (IOException | InterruptedException e) {
            throw new CommException(COMM_MSG, e);
        }

        InputStream body = httpUtils.processResponse(response);

        if (response.statusCode() != StatusCode.OK.getCode()) {
            throw errorResponseExceptionFactory.forTokenEndpoint(response, body);
        }

        OpenIdToken openIdToken;
        try {
            openIdToken = openIdTokenReader.readValue(body);
        } catch (IOException e) {
            throw new TranslateException(ERROR_MSG, e);
        }

        return openIdToken;
    }

    public JsonWebToken<User> getUser(String accessToken) throws CommException, TranslateException, ErrorResponseException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(endpointProps.getUserInfoEndpoint())
                .timeout(Duration.ofSeconds(2))
                .header(Header.ACCEPT.toString(), ContentType.JWT.toString())
                .header(Header.AUTHORIZATION.toString(), String.format(BEARER_AUTH, accessToken))
                .header(Header.ACCEPT_ENCODING.toString(), "gzip")
                .header(Header.CONTENT_TYPE.toString(), ContentType.JSON_UTF_8.toString())
                .header(Header.CORRELATION_ID.toString(), httpUtils.getCorrelationId())
                .header(Header.LOGIN_SDK.toString(), HeaderValue.LOGIN_SDK.toString())
                .header(Header.LOGIN_SDK_VERSION.toString(), HeaderValue.LOGIN_SDK_VERSION.toString())
                .GET()
                .build();

        HttpResponse<InputStream> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (IOException | InterruptedException e) {
            throw new CommException(COMM_MSG, e);
        }

        if (response.statusCode() != StatusCode.OK.getCode()) {
            throw errorResponseExceptionFactory.forUserEndpoint(response.statusCode());
        }

        InputStream body = httpUtils.processResponse(response);
        String idToken = httpUtils.to(body);

        JwtSerde jwtSerializer = jwtAppFactory.jwtSerde();
        JsonWebToken<User> jwt;
        try {
            jwt = jwtSerializer.stringToJwt(idToken, User.class);
        } catch (JsonToJwtException | InvalidJWT e) {
            throw new TranslateException(JWT_MSG, e);
        }

        return jwt;
    }


    public Map<String, List<String>> makePasswordForm(String username, String password, List<String> scopes) {
        HashMap<String, List<String>> form = new HashMap<>();
        form.put(FormFields.GRANT_TYPE.toString(), Arrays.asList(GrantType.PASSWORD.toString()));
        form.put(FormFields.USERNAME.toString(), Arrays.asList(username));
        form.put(FormFields.PASSWORD.toString(), Arrays.asList(password));

        String scope = scopes.stream().collect(Collectors.joining(Delimitter.SPACE.toString()));
        form.put(FormFields.SCOPE.toString(), Arrays.asList(scope));

        return form;
    }

    public Map<String, List<String>> makeRefreshForm(String refreshToken) {
        HashMap<String, List<String>> form = new HashMap<>();
        form.put(FormFields.GRANT_TYPE.toString(), Arrays.asList(GrantType.REFRESH_TOKEN.toString()));
        form.put(FormFields.REFRESH_TOKEN.toString(), Arrays.asList(refreshToken));

        return form;
    }

    public Map<String, List<String>> makeCodeForm(String code, String redirectUri) {
        Map<String, List<String>> form = new HashMap<>();
        form.put(FormFields.GRANT_TYPE.toString(), Arrays.asList(GrantType.AUTHORIZATION_CODE.toString()));
        form.put(FormFields.CODE.toString(), Arrays.asList(code));
        form.put(FormFields.REDIRECT_URI.toString(), Arrays.asList(redirectUri));

        return form;
    }
}
