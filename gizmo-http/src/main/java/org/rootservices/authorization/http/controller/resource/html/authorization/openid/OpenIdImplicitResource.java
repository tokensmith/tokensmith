package org.rootservices.authorization.http.controller.resource.html.authorization.openid;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.exception.ServerException;
import org.rootservices.authorization.http.controller.resource.html.authorization.helper.AuthorizationHelper;
import org.rootservices.authorization.http.controller.security.TokenSession;
import org.rootservices.authorization.http.controller.security.WebSiteUser;
import org.rootservices.authorization.http.presenter.AuthorizationPresenter;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.ValidateOpenIdIdImplicitGrant;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response.RequestOpenIdImplicitTokenAndIdentity;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response.entity.OpenIdImplicitAccessToken;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.controller.header.ContentType;
import org.rootservices.otter.controller.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenIdImplicitResource extends Resource<TokenSession, WebSiteUser> {
    private static final Logger logger = LogManager.getLogger(OpenIdImplicitResource.class);

    private static String JSP_PATH = "/WEB-INF/jsp/authorization.jsp";
    protected static String EMAIL = "email";
    protected static String PASSWORD = "password";
    protected static String BLANK = "";

    private AuthorizationHelper authorizationHelper;
    private ValidateOpenIdIdImplicitGrant validateOpenIdIdImplicitGrant;
    private RequestOpenIdImplicitTokenAndIdentity requestOpenIdImplicitTokenAndIdentity;

    public OpenIdImplicitResource() {}

    @Autowired
    public OpenIdImplicitResource(AuthorizationHelper authorizationHelper, ValidateOpenIdIdImplicitGrant validateOpenIdIdImplicitGrant, RequestOpenIdImplicitTokenAndIdentity requestOpenIdImplicitTokenAndIdentity) {
        this.authorizationHelper = authorizationHelper;
        this.validateOpenIdIdImplicitGrant = validateOpenIdIdImplicitGrant;
        this.requestOpenIdImplicitTokenAndIdentity = requestOpenIdImplicitTokenAndIdentity;
    }

    @Override
    public Response<TokenSession> get(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        try {
            validateOpenIdIdImplicitGrant.run(request.getQueryParams());
        } catch (InformResourceOwnerException e) {
            authorizationHelper.prepareNotFoundResponse(response);
            return response;
        } catch (InformClientException e) {
            authorizationHelper.prepareErrorResponse(response, e.getRedirectURI(), e.getError(), e.getDescription(), e.getState());
            return response;
        } catch (ServerException e) {
            logger.error(e.getMessage(), e);
            authorizationHelper.prepareServerErrorResponse(response);
            return response;
        }

        AuthorizationPresenter presenter = authorizationHelper.makeAuthorizationPresenter(BLANK, request.getCsrfChallenge().get());
        authorizationHelper.prepareResponse(response, StatusCode.OK, presenter, JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> post(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        String userName = authorizationHelper.getFormValue(request.getFormData().get(EMAIL));
        String password = authorizationHelper.getFormValue(request.getFormData().get(PASSWORD));

        OpenIdImplicitAccessToken accessToken;
        try {
            accessToken = requestOpenIdImplicitTokenAndIdentity.request(userName, password, request.getQueryParams());
        } catch (UnauthorizedException e) {
            AuthorizationPresenter presenter = authorizationHelper.makeAuthorizationPresenter(userName, request.getCsrfChallenge().get());
            authorizationHelper.prepareResponse(response, StatusCode.FORBIDDEN, presenter, JSP_PATH);
            return response;
        } catch (InformResourceOwnerException e) {
            authorizationHelper.prepareNotFoundResponse(response);
            return response;
        } catch (InformClientException e) {
            authorizationHelper.prepareErrorResponse(response, e.getRedirectURI(), e.getError(), e.getDescription(), e.getState());
            return response;
        } catch (ServerException e) {
            logger.error(e.getMessage(), e);
            authorizationHelper.prepareServerErrorResponse(response);
            return response;
        }

        response.getHeaders().put(Header.CONTENT_TYPE.getValue(), ContentType.FORM_URL_ENCODED.getValue());
        String location = authorizationHelper.makeRedirectURIForOpenIdImplicit(accessToken);
        response.getHeaders().put(Header.LOCATION.getValue(), location);
        response.setStatusCode(StatusCode.MOVED_TEMPORARILY);

        return response;
    }
}
