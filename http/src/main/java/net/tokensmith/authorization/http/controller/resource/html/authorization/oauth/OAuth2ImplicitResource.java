package net.tokensmith.authorization.http.controller.resource.html.authorization.oauth;


import net.tokensmith.authorization.authenticate.exception.UnauthorizedException;
import net.tokensmith.authorization.exception.ServerException;
import net.tokensmith.authorization.http.controller.resource.html.CookieName;
import net.tokensmith.authorization.http.controller.resource.html.authorization.helper.AuthorizationHelper;
import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import net.tokensmith.authorization.http.presenter.AuthorizationPresenter;
import net.tokensmith.authorization.http.service.CookieService;
import net.tokensmith.authorization.oauth2.grant.redirect.implicit.authorization.request.ValidateImplicitGrant;
import net.tokensmith.authorization.oauth2.grant.redirect.implicit.authorization.response.RequestAccessToken;
import net.tokensmith.authorization.oauth2.grant.redirect.implicit.authorization.response.entity.ImplicitAccessToken;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.controller.header.ContentType;
import net.tokensmith.otter.controller.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OAuth2ImplicitResource extends Resource<WebSiteSession, WebSiteUser> {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2ImplicitResource.class);

    private static String JSP_PATH = "/WEB-INF/jsp/authorization.jsp";
    protected static String EMAIL = "email";
    protected static String PASSWORD = "password";
    protected static String BLANK = "";

    private String globalCssPath;
    private AuthorizationHelper authorizationHelper;
    private ValidateImplicitGrant validateImplicitGrant;
    private RequestAccessToken requestAccessToken;
    private CookieService cookieService;

    public OAuth2ImplicitResource() {}

    @Autowired
    public OAuth2ImplicitResource(String globalCssPath, AuthorizationHelper authorizationHelper, ValidateImplicitGrant validateImplicitGrant, RequestAccessToken requestAccessToken, CookieService cookieService) {
        this.globalCssPath = globalCssPath;
        this.authorizationHelper = authorizationHelper;
        this.validateImplicitGrant = validateImplicitGrant;
        this.requestAccessToken = requestAccessToken;
        this.cookieService = cookieService;
    }

    @Override
    public Response<WebSiteSession> get(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        try {
            validateImplicitGrant.run(request.getQueryParams());
        } catch (InformResourceOwnerException e) {
            authorizationHelper.prepareNotFoundResponse(globalCssPath, response);
            return response;
        } catch (InformClientException e) {
            authorizationHelper.prepareErrorResponse(response, e.getRedirectURI(), e.getError(), e.getDescription(), e.getState());
            return response;
        } catch (ServerException e) {
            logger.error(e.getMessage(), e);
            authorizationHelper.prepareServerErrorResponse(globalCssPath, response);
            return response;
        }


        AuthorizationPresenter presenter = authorizationHelper.makeAuthorizationPresenter(globalCssPath, BLANK, request.getCsrfChallenge().get());
        cookieService.manageRedirectForAuth(presenter, request, response);
        authorizationHelper.prepareResponse(response, StatusCode.OK, presenter, JSP_PATH);
        return response;
    }

    @Override
    public Response<WebSiteSession> post(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        String userName = authorizationHelper.getFormValue(request.getFormData().get(EMAIL));
        String password = authorizationHelper.getFormValue(request.getFormData().get(PASSWORD));

        ImplicitAccessToken accessToken;
        try {
            accessToken = requestAccessToken.requestToken(userName, password, request.getQueryParams());
        } catch (UnauthorizedException e) {
            AuthorizationPresenter presenter = authorizationHelper.makeAuthorizationPresenter(globalCssPath, userName, request.getCsrfChallenge().get());
            authorizationHelper.prepareResponse(response, StatusCode.FORBIDDEN, presenter, JSP_PATH);
            return response;
        } catch (InformResourceOwnerException e) {
            authorizationHelper.prepareNotFoundResponse(globalCssPath, response);
            response.getCookies().remove(CookieName.REDIRECT.toString());
            return response;
        } catch (InformClientException e) {
            authorizationHelper.prepareErrorResponse(response, e.getRedirectURI(), e.getError(), e.getDescription(), e.getState());
            response.getCookies().remove(CookieName.REDIRECT.toString());
            return response;
        } catch (ServerException e) {
            logger.error(e.getMessage(), e);
            authorizationHelper.prepareServerErrorResponse(globalCssPath, response);
            response.getCookies().remove(CookieName.REDIRECT.toString());
            return response;
        }

        response.getHeaders().put(Header.CONTENT_TYPE.getValue(), ContentType.FORM_URL_ENCODED.getValue());
        String location = authorizationHelper.makeRedirectURIForImplicit(accessToken);
        response.getHeaders().put(Header.LOCATION.getValue(), location);
        response.setStatusCode(StatusCode.MOVED_TEMPORARILY);

        WebSiteSession session = new WebSiteSession(
            accessToken.getSessionToken(),
            accessToken.getSessionTokenIssuedAt()
        );
        response.setSession(Optional.of(session));
        response.getCookies().remove(CookieName.REDIRECT.toString());

        return response;
    }
}
