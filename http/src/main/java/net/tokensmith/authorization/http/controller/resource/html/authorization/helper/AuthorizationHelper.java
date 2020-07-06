package net.tokensmith.authorization.http.controller.resource.html.authorization.helper;


import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.presenter.AssetPresenter;
import net.tokensmith.authorization.http.presenter.AuthorizationPresenter;
import net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response.AuthResponse;
import net.tokensmith.authorization.oauth2.grant.redirect.implicit.authorization.response.entity.ImplicitAccessToken;
import net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.response.entity.OpenIdImplicitAccessToken;
import net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.response.entity.OpenIdImplicitIdentity;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.controller.header.ContentType;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.otter.security.cookie.CookieSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;


@Component
public class AuthorizationHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationHelper.class);
    private static String NOT_FOUND_JSP_PATH = "/WEB-INF/jsp/404.jsp";
    private static String SERVER_ERROR_JSP_PATH = "/WEB-INF/jsp/500.jsp";

    // statics to help making the redirects.
    private static String ERROR = "error=%s";
    private static String ERROR_DESC = "error_description=%s";
    private static String STATE = "state=%s";
    private static String CODE = "code=%s";
    private static String ACCESS_TOKEN = "access_token=%s";
    private static String EXPIRES_IN = "expires_in=%s";
    private static String ID_TOKEN = "id_token=%s";
    private static String TOKEN_TYPE = "token_type=%s";
    private static String SCOPE = "scope=%s";
    private static String BEGIN = "?";
    private static String AND = "&";

    public String getFormValue(List<String> formValue) {
        String value = null;
        if (formValue != null && formValue.size() == 1) {
            value = formValue.get(0);
        }
        return value;
    }

    public void prepareErrorResponse(Response<WebSiteSession> response, URI redirect, String error, String desc, Optional<String> state) {
        response.getHeaders().put(Header.CONTENT_TYPE.getValue(), ContentType.FORM_URL_ENCODED.getValue());

        StringBuilder location = new StringBuilder();
        location.append(redirect);
        location.append(BEGIN);
        location.append(String.format(ERROR, error));
        location.append(AND);
        location.append(String.format(ERROR_DESC, desc));

        if (state.isPresent()) {
            location.append(AND);
            location.append(String.format(STATE, state.get()));
        }

        response.setStatusCode(StatusCode.MOVED_TEMPORARILY);
        response.getHeaders().put(Header.LOCATION.getValue(), location.toString());
    }

    public void prepareNotFoundResponse(String globalCssPath, Response<WebSiteSession> response) {
        AssetPresenter presenter = new AssetPresenter(globalCssPath);
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.NOT_FOUND);
        response.setTemplate(Optional.of(NOT_FOUND_JSP_PATH));
    }

    public void prepareServerErrorResponse(String globalCssPath, Response<WebSiteSession> response) {
        AssetPresenter presenter = new AssetPresenter();
        presenter.setGlobalCssPath(globalCssPath);
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setTemplate(Optional.of(SERVER_ERROR_JSP_PATH));
    }

    public AuthorizationPresenter makeAuthorizationPresenter(String globalCssPath, String defaultEmail, String csrfToken) {
        AuthorizationPresenter presenter = new AuthorizationPresenter();
        presenter.setGlobalCssPath(globalCssPath);
        presenter.setEmail(defaultEmail);
        presenter.setEncodedCsrfToken(csrfToken);
        presenter.setUserMessage(Optional.empty());
        return presenter;
    }

    public void prepareResponse(Response<WebSiteSession> response, StatusCode statusCode, AuthorizationPresenter presenter, String template) {
        response.setStatusCode(statusCode);
        response.setPresenter(Optional.of(presenter));
        response.setTemplate(Optional.of(template));
    }

    public String makeRedirectURIForCodeGrant(AuthResponse authResponse) {
        StringBuilder location = new StringBuilder();

        location.append(authResponse.getRedirectUri());
        location.append(BEGIN);
        location.append(String.format(CODE, authResponse.getCode()));

        if (authResponse.getState().isPresent()) {
            location.append(AND);
            location.append(String.format(STATE, authResponse.getState().get()));
        }

        return location.toString();
    }

    public String makeRedirectURIForImplicit(ImplicitAccessToken accessToken) {
        StringBuilder location = new StringBuilder();
        location.append(accessToken.getRedirectUri().toString());
        location.append(BEGIN);
        location.append(String.format(ACCESS_TOKEN, accessToken.getAccessToken()));
        location.append(AND);
        location.append(String.format(EXPIRES_IN, accessToken.getExpiresIn()));

        if (accessToken.getState().isPresent()) {
            location.append(AND);
            location.append(String.format(STATE, accessToken.getState().get()));
        }

        if (accessToken.getScope().isPresent()) {
            try {
                location.append(AND);
                location.append(String.format(SCOPE, URLEncoder.encode(accessToken.getScope().get(), StandardCharsets.UTF_8.name())));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return location.toString();
    }

    public String makeRedirectURIForOpenIdIdentity(OpenIdImplicitIdentity identity) {
        StringBuilder location = new StringBuilder();
        location.append(identity.getRedirectUri().toString());
        location.append(BEGIN);
        location.append(String.format(ID_TOKEN, identity.getIdToken()));


        if (identity.getState().isPresent()) {
            location.append(AND);
            location.append(String.format(STATE, identity.getState().get()));
        }

        if (identity.getScope().isPresent()) {
            try {
                location.append(AND);
                location.append(String.format(SCOPE, URLEncoder.encode(identity.getScope().get(), StandardCharsets.UTF_8.name())));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return location.toString();
    }

    public String makeRedirectURIForOpenIdImplicit(OpenIdImplicitAccessToken accessToken) {
        StringBuilder location = new StringBuilder();
        location.append(accessToken.getRedirectUri().toString());
        location.append(BEGIN);

        location.append(String.format(ACCESS_TOKEN, accessToken.getAccessToken()));
        location.append(AND);
        location.append(String.format(TOKEN_TYPE, accessToken.getTokenType().toString().toLowerCase()));
        location.append(AND);
        location.append(String.format(ID_TOKEN, accessToken.getIdToken()));
        location.append(AND);
        location.append(String.format(EXPIRES_IN, accessToken.getExpiresIn()));

        if (accessToken.getState().isPresent()) {
            location.append(AND);
            location.append(String.format(STATE, accessToken.getState().get()));
        }

        if (accessToken.getScope().isPresent()) {
            try {
                location.append(AND);
                location.append(String.format(SCOPE, URLEncoder.encode(accessToken.getScope().get(), StandardCharsets.UTF_8.name())));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return location.toString();
    }
}
