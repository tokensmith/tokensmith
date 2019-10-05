package org.rootservices.authorization.http.controller.resource.authorization.welcome;

import org.rootservices.authorization.exception.BadRequestException;
import org.rootservices.authorization.exception.NotFoundException;
import org.rootservices.authorization.http.controller.security.TokenSession;
import org.rootservices.authorization.http.controller.security.WebSiteUser;
import org.rootservices.authorization.nonce.welcome.Welcome;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class WelcomeResource extends Resource<TokenSession, WebSiteUser> {
    private static String JSP_PATH_OK = "/WEB-INF/jsp/welcome.jsp";
    private static String JSP_PATH_ERROR = "/WEB-INF/jsp/welcome-error.jsp";
    public String URL = "/welcome\\?nonce=(.*)";
    private static String NONCE_URL_PARAM = "nonce";

    private String NONCE_URL_ERROR_MSG = "input was null, empty, or had more than 1 value";
    private Welcome welcome;

    public WelcomeResource() {}

    @Autowired
    public WelcomeResource(Welcome welcome) {
        this.welcome = welcome;
    }

    @Override
    public Response<TokenSession> get(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {

        String nonce;
        try {
            nonce = getNonceUrlValue(request.getQueryParams().get(NONCE_URL_PARAM));
        } catch (InvalidParamException e) {
            response.setTemplate(Optional.of(JSP_PATH_ERROR));
            response.setStatusCode(StatusCode.BAD_REQUEST);
            return response;
        }

        try {
            welcome.markEmailVerified(nonce);
        } catch (BadRequestException e) {
            response.setTemplate(Optional.of(JSP_PATH_ERROR));
            response.setStatusCode(StatusCode.BAD_REQUEST);
            return response;
        } catch(NotFoundException e) {
            response.setTemplate(Optional.of(JSP_PATH_ERROR));
            response.setStatusCode(StatusCode.OK);
            return response;
        }

        response.setTemplate(Optional.of(JSP_PATH_OK));
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    protected String getNonceUrlValue(List<String> values) throws InvalidParamException{
        if (values == null || values.size() == 0 || values.size() > 1) {
            throw new InvalidParamException(NONCE_URL_ERROR_MSG);
        }
        return values.get(0);
    }
}
