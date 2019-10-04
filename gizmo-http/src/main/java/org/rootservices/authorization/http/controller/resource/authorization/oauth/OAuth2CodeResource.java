package org.rootservices.authorization.http.controller.resource.authorization.oauth;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.exception.ServerException;
import org.rootservices.authorization.http.controller.resource.authorization.helper.AuthorizationHelper;
import org.rootservices.authorization.http.presenter.AuthorizationPresenter;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.request.ValidateCodeGrant;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.AuthResponse;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.RequestAuthCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.header.ContentType;
import org.rootservices.otter.controller.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class OAuth2CodeResource extends Resource {
    private static final Logger logger = LogManager.getLogger(OAuth2CodeResource.class);

    private static String JSP_PATH = "/WEB-INF/jsp/authorization.jsp";
    protected static String EMAIL = "email";
    protected static String PASSWORD = "password";
    protected static String BLANK = "";

    private AuthorizationHelper authorizationHelper;
    private ValidateCodeGrant validateCodeGrant;
    private RequestAuthCode requestAuthCode;

    public OAuth2CodeResource() {}

    @Autowired
    public OAuth2CodeResource(AuthorizationHelper authorizationHelper, ValidateCodeGrant validateCodeGrant, RequestAuthCode requestAuthCode) {
        this.authorizationHelper = authorizationHelper;
        this.validateCodeGrant = validateCodeGrant;
        this.requestAuthCode = requestAuthCode;
    }

    @Override
    public Response get(Request request, Response response) {
        try {
            validateCodeGrant.run(request.getQueryParams());
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
    public Response post(Request request, Response response) {
        String userName = authorizationHelper.getFormValue(request.getFormData().get(EMAIL));
        String password = authorizationHelper.getFormValue(request.getFormData().get(PASSWORD));

        AuthResponse authResponse;
        try {
            authResponse = requestAuthCode.run(userName, password, request.getQueryParams());
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
        String location = authorizationHelper.makeRedirectURIForCodeGrant(authResponse);
        response.getHeaders().put(Header.LOCATION.getValue(), location);
        response.setStatusCode(StatusCode.MOVED_TEMPORARILY);

        return response;
    }
}
