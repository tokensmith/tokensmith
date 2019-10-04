package org.rootservices.authorization.http.controller.resource.authorization.helper;

import helpers.category.UnitTests;
import helpers.fixture.EntityFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.authorization.http.presenter.AuthorizationPresenter;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.AuthResponse;
import org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response.entity.ImplicitAccessToken;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response.entity.OpenIdImplicitAccessToken;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response.entity.OpenIdImplicitIdentity;
import org.rootservices.otter.controller.builder.ResponseBuilder;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.header.Header;

import java.net.URI;
import java.util.HashMap;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;


@Category(UnitTests.class)
public class AuthorizationHelperTest {
    private AuthorizationHelper subject;

    @Before
    public void setUp() {
        subject = new AuthorizationHelper();
    }

    @Test
    public void prepareErrorResponseWhenNoState() throws Exception {
        Response response = new ResponseBuilder().headers(new HashMap<>()).build();
        URI redirect = new URI("https://rootservices.org");
        String error = "some-error";
        String desc = "some-description";
        Optional<String> state = Optional.empty();

        subject.prepareErrorResponse(response, redirect, error, desc, state);

        assertThat(response.getStatusCode(), is(StatusCode.MOVED_TEMPORARILY));
        assertThat(response.getHeaders(), is(notNullValue()));
        assertThat(response.getHeaders().get(Header.LOCATION.getValue()), is(notNullValue()));
        assertThat(response.getHeaders().get(Header.LOCATION.getValue()), is("https://rootservices.org?error=some-error&error_description=some-description"));
    }

    @Test
    public void prepareErrorResponseWhenState() throws Exception {
        Response response = new ResponseBuilder().headers(new HashMap<>()).build();
        URI redirect = new URI("https://rootservices.org");
        String error = "some-error";
        String desc = "some-description";
        Optional<String> state = Optional.of("some-state");

        subject.prepareErrorResponse(response, redirect, error, desc, state);

        assertThat(response.getStatusCode(), is(StatusCode.MOVED_TEMPORARILY));
        assertThat(response.getHeaders(), is(notNullValue()));
        assertThat(response.getHeaders().get(Header.LOCATION.getValue()), is(notNullValue()));
        assertThat(response.getHeaders().get(Header.LOCATION.getValue()), is("https://rootservices.org?error=some-error&error_description=some-description&state=some-state"));
    }

    @Test
    public void prepareNotFoundResponse() {
        Response response = new ResponseBuilder().headers(new HashMap<>()).build();

        subject.prepareNotFoundResponse(response);

        assertThat(response.getStatusCode(), is(StatusCode.NOT_FOUND));
        assertThat(response.getTemplate().isPresent(), is(true));
        assertThat(response.getTemplate().get(), is("/WEB-INF/jsp/404.jsp"));
    }

    @Test
    public void prepareServerErrorResponse() {
        Response response = new ResponseBuilder().headers(new HashMap<>()).build();

        subject.prepareServerErrorResponse(response);

        assertThat(response.getStatusCode(), is(StatusCode.SERVER_ERROR));
        assertThat(response.getTemplate().isPresent(), is(true));
        assertThat(response.getTemplate().get(), is("/WEB-INF/jsp/500.jsp"));
    }

    @Test
    public void makeAuthorizationPresenter() {
        String email = "obi-wan@rootservices.org";
        String csrf = "csrf-token";

        AuthorizationPresenter actual = subject.makeAuthorizationPresenter(email, csrf);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getEmail(), is(email));
        assertThat(actual.getEncodedCsrfToken(), is(csrf));
    }

    @Test
    public void prepareResponse() {
        Response response = new ResponseBuilder().headers(new HashMap<>()).build();
        AuthorizationPresenter presenter = new AuthorizationPresenter();
        String template = "/path/to/template";

        subject.prepareResponse(response, StatusCode.OK, presenter, template);

        assertThat(response.getStatusCode(), is(StatusCode.OK));
        assertThat(response.getPresenter().isPresent(), is(true));
        assertThat(response.getPresenter().get(), is(presenter));
        assertThat(response.getTemplate().isPresent(), is(true));
        assertThat(response.getTemplate().get(), is(template));
    }

    @Test
    public void makeRedirectURIForCodeGrantWhenNoState() throws Exception {
        AuthResponse response = new AuthResponse(new URI("https://rootservices.org"), "code", Optional.empty());

        String actual = subject.makeRedirectURIForCodeGrant(response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is("https://rootservices.org?code=code"));
    }

    @Test
    public void makeRedirectURIForCodeGrantWhenState() throws Exception {
        AuthResponse response = new AuthResponse(new URI("https://rootservices.org"), "code", Optional.of("some-state"));

        String actual = subject.makeRedirectURIForCodeGrant(response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is("https://rootservices.org?code=code&state=some-state"));
    }

    @Test
    public void makeRedirectURIForImplicitWhenNoStateNoScope() throws Exception {
        ImplicitAccessToken accessToken = new ImplicitAccessToken(
                new URI("https://rootservices.org"), "access-token", TokenType.BEARER, 100L, Optional.empty(), Optional.empty()
        );

        String actual = subject.makeRedirectURIForImplicit(accessToken);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is("https://rootservices.org?access_token=access-token&expires_in=100"));
    }

    @Test
    public void makeRedirectURIForImplicitWhenNoStateAndScope() throws Exception {
        ImplicitAccessToken accessToken = new ImplicitAccessToken(
                new URI("https://rootservices.org"), "access-token", TokenType.BEARER, 100L, Optional.of("profile email"), Optional.empty()
        );

        String actual = subject.makeRedirectURIForImplicit(accessToken);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is("https://rootservices.org?access_token=access-token&expires_in=100&scope=profile+email"));
    }

    @Test
    public void makeRedirectURIForImplicitWhenState() throws Exception {
        ImplicitAccessToken accessToken = new ImplicitAccessToken(
                new URI("https://rootservices.org"), "access-token", TokenType.BEARER, 100L, Optional.empty(), Optional.of("some-state")
        );

        String actual = subject.makeRedirectURIForImplicit(accessToken);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is("https://rootservices.org?access_token=access-token&expires_in=100&state=some-state"));
    }

    @Test
    public void makeRedirectURIForImplicitWhenStateAndScope() throws Exception {
        ImplicitAccessToken accessToken = new ImplicitAccessToken(
                new URI("https://rootservices.org"), "access-token", TokenType.BEARER, 100L, Optional.of("profile email"), Optional.of("some-state")
        );

        String actual = subject.makeRedirectURIForImplicit(accessToken);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is("https://rootservices.org?access_token=access-token&expires_in=100&state=some-state&scope=profile+email"));
    }

    @Test
    public void makeRedirectURIForOpenIdIdentityWhenNoStateNoScope() throws Exception {
        OpenIdImplicitIdentity accessToken = new OpenIdImplicitIdentity();
        accessToken.setRedirectUri(new URI(EntityFactory.SECURE_REDIRECT_URI));
        accessToken.setState(Optional.empty());
        accessToken.setScope(Optional.empty());
        accessToken.setIdToken("idToken");

        String actual = subject.makeRedirectURIForOpenIdIdentity(accessToken);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is("https://rootservices.org?id_token=idToken"));
    }

    @Test
    public void makeRedirectURIForOpenIdIdentityWhenNoStateAndScope() throws Exception {
        OpenIdImplicitIdentity accessToken = new OpenIdImplicitIdentity();
        accessToken.setRedirectUri(new URI(EntityFactory.SECURE_REDIRECT_URI));
        accessToken.setState(Optional.empty());
        accessToken.setScope(Optional.of("profile email"));
        accessToken.setIdToken("idToken");

        String actual = subject.makeRedirectURIForOpenIdIdentity(accessToken);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is("https://rootservices.org?id_token=idToken&scope=profile+email"));
    }

    @Test
    public void makeRedirectURIForOpenIdIdentityWhenState() throws Exception {
        OpenIdImplicitIdentity accessToken = new OpenIdImplicitIdentity();
        accessToken.setRedirectUri(new URI(EntityFactory.SECURE_REDIRECT_URI));
        accessToken.setState(Optional.of("state"));
        accessToken.setScope(Optional.empty());
        accessToken.setIdToken("idToken");

        String actual = subject.makeRedirectURIForOpenIdIdentity(accessToken);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is("https://rootservices.org?id_token=idToken&state=state"));
    }

    @Test
    public void makeRedirectURIForOpenIdIdentityWhenStateAndScope() throws Exception {
        OpenIdImplicitIdentity accessToken = new OpenIdImplicitIdentity();
        accessToken.setRedirectUri(new URI(EntityFactory.SECURE_REDIRECT_URI));
        accessToken.setState(Optional.of("state"));
        accessToken.setScope(Optional.of("profile email"));
        accessToken.setIdToken("idToken");

        String actual = subject.makeRedirectURIForOpenIdIdentity(accessToken);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is("https://rootservices.org?id_token=idToken&state=state&scope=profile+email"));
    }

    @Test
    public void makeRedirectURIForOpenIdImplicitWhenNoStateAndNoScope() throws Exception {
        OpenIdImplicitAccessToken accessToken = new OpenIdImplicitAccessToken();
        accessToken.setRedirectUri(new URI(EntityFactory.SECURE_REDIRECT_URI));
        accessToken.setAccessToken("access-token");
        accessToken.setExpiresIn(3600L);
        accessToken.setTokenType(TokenType.BEARER);
        accessToken.setState(Optional.empty());
        accessToken.setScope(Optional.empty());
        accessToken.setIdToken("idToken");

        String actual = subject.makeRedirectURIForOpenIdImplicit(accessToken);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is("https://rootservices.org?access_token=access-token&token_type=bearer&id_token=idToken&expires_in=3600"));
    }

    @Test
    public void makeRedirectURIForOpenIdImplicitWhenNoStateAndScope() throws Exception {
        OpenIdImplicitAccessToken accessToken = new OpenIdImplicitAccessToken();
        accessToken.setRedirectUri(new URI(EntityFactory.SECURE_REDIRECT_URI));
        accessToken.setAccessToken("access-token");
        accessToken.setExpiresIn(3600L);
        accessToken.setTokenType(TokenType.BEARER);
        accessToken.setState(Optional.empty());
        accessToken.setScope(Optional.of("profile email"));
        accessToken.setIdToken("idToken");

        String actual = subject.makeRedirectURIForOpenIdImplicit(accessToken);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is("https://rootservices.org?access_token=access-token&token_type=bearer&id_token=idToken&expires_in=3600&scope=profile+email"));
    }

    @Test
    public void makeRedirectURIForOpenIdImplicitWhenState() throws Exception {
        OpenIdImplicitAccessToken accessToken = new OpenIdImplicitAccessToken();
        accessToken.setRedirectUri(new URI(EntityFactory.SECURE_REDIRECT_URI));
        accessToken.setAccessToken("access-token");
        accessToken.setExpiresIn(3600L);
        accessToken.setTokenType(TokenType.BEARER);
        accessToken.setState(Optional.of("state"));
        accessToken.setScope(Optional.empty());
        accessToken.setIdToken("idToken");

        String actual = subject.makeRedirectURIForOpenIdImplicit(accessToken);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is("https://rootservices.org?access_token=access-token&token_type=bearer&id_token=idToken&expires_in=3600&state=state"));
    }

    @Test
    public void makeRedirectURIForOpenIdImplicitWhenStateAndScope() throws Exception {
        OpenIdImplicitAccessToken accessToken = new OpenIdImplicitAccessToken();
        accessToken.setRedirectUri(new URI(EntityFactory.SECURE_REDIRECT_URI));
        accessToken.setAccessToken("access-token");
        accessToken.setExpiresIn(3600L);
        accessToken.setTokenType(TokenType.BEARER);
        accessToken.setState(Optional.of("state"));
        accessToken.setScope(Optional.of("profile email"));
        accessToken.setIdToken("idToken");

        String actual = subject.makeRedirectURIForOpenIdImplicit(accessToken);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is("https://rootservices.org?access_token=access-token&token_type=bearer&id_token=idToken&expires_in=3600&state=state&scope=profile+email"));
    }
}