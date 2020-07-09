package net.tokensmith.authorization.http.controller.resource.html.authorization.welcome;


import net.tokensmith.authorization.http.controller.resource.html.authorization.exception.InvalidParamException;
import net.tokensmith.authorization.http.presenter.AssetPresenter;
import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.authorization.exception.BadRequestException;
import net.tokensmith.authorization.exception.NotFoundException;
import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import net.tokensmith.authorization.nonce.welcome.Welcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class WelcomeResource extends Resource<WebSiteSession, WebSiteUser> {
    private static String JSP_PATH_OK = "/WEB-INF/jsp/welcome/welcome.jsp";
    private static String JSP_PATH_ERROR = "/WEB-INF/jsp/welcome/welcome-error.jsp";
    public static String URL = "/welcome\\?nonce=(.*)";
    private static String NONCE_URL_PARAM = "nonce";

    private String NONCE_URL_ERROR_MSG = "input was null, empty, or had more than 1 value";
    private String globalCssPath;
    private Welcome welcome;

    public WelcomeResource() {}

    @Autowired
    public WelcomeResource(String globalCssPath, Welcome welcome) {
        this.globalCssPath = globalCssPath;
        this.welcome = welcome;
    }

    @Override
    public Response<WebSiteSession> get(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {

        String nonce;
        try {
            nonce = getNonceUrlValue(request.getQueryParams().get(NONCE_URL_PARAM));
        } catch (InvalidParamException e) {
            prepareResponse(response, StatusCode.BAD_REQUEST, JSP_PATH_ERROR);
            return response;
        }

        try {
            welcome.markEmailVerified(nonce);
        } catch (BadRequestException e) {
            // nonce was invalid.
            prepareResponse(response, StatusCode.BAD_REQUEST, JSP_PATH_ERROR);
            return response;
        } catch(NotFoundException e) {
            prepareResponse(response, StatusCode.OK, JSP_PATH_ERROR);
            response.setTemplate(Optional.of(JSP_PATH_ERROR));
            return response;
        }

        prepareResponse(response, StatusCode.OK, JSP_PATH_OK);
        return response;
    }

    protected String getNonceUrlValue(List<String> values) throws InvalidParamException{
        if (values == null || values.size() == 0 || values.size() > 1) {
            throw new InvalidParamException(NONCE_URL_ERROR_MSG);
        }
        return values.get(0);
    }

    protected void prepareResponse(Response<WebSiteSession> response, StatusCode statusCode, String template) {
        AssetPresenter presenter = new AssetPresenter(globalCssPath);
        response.setStatusCode(statusCode);
        response.setPresenter(Optional.of(presenter));
        response.setTemplate(Optional.of(template));
    }
}
