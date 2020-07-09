package net.tokensmith.authorization.http.controller.resource.html;

import net.tokensmith.authorization.http.controller.resource.html.authorization.exception.InvalidParamException;
import net.tokensmith.authorization.http.presenter.AssetPresenter;
import net.tokensmith.authorization.register.exception.NonceException;
import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import net.tokensmith.authorization.exception.BadRequestException;
import net.tokensmith.authorization.exception.NotFoundException;
import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import net.tokensmith.authorization.http.presenter.UpdatePasswordPresenter;
import net.tokensmith.authorization.nonce.reset.ForgotPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Component
public class UpdatePasswordResource extends Resource<WebSiteSession, WebSiteUser> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatePasswordResource.class);
    public static String URL = "/update-password\\?nonce=(.*)";

    private static String JSP_FORM_PATH = "/WEB-INF/jsp/password/update-password.jsp";
    private static String JSP_OK_PATH = "/WEB-INF/jsp/password/update-password-ok.jsp";
    private static String JSP_EXPIRED_PATH = "/WEB-INF/jsp/password/update-password-expired.jsp";
    private static String JSP_PATH_ERROR = "/WEB-INF/jsp/password/update-password-error.jsp";

    private String NONCE_URL_ERROR_MSG = "input was null, empty, or had more than 1 value";

    private String globalCssPath;
    private ForgotPassword forgotPassword;

    private static String NONCE_URL_PARAM = "nonce";
    private static String PASSWORD = "password";
    private static String REPEAT_PASSWORD = "repeatPassword";

    @Autowired
    public UpdatePasswordResource(String globalCssPath, ForgotPassword forgotPassword) {
        this.globalCssPath = globalCssPath;
        this.forgotPassword = forgotPassword;
    }

    @Override
    public Response<WebSiteSession> get(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {

        UpdatePasswordPresenter presenter = presenter(request.getCsrfChallenge().get());

        // basic verification that the nonce key is there.
        String nonce;
        try {
            nonce = getNonceUrlValue(request.getQueryParams().get(NONCE_URL_PARAM));
        } catch (InvalidParamException e) {
            prepareResponse(response, presenter, StatusCode.BAD_REQUEST, JSP_PATH_ERROR);
            return response;
        }

        // makes sure nonce is a jwt.
        try {
            forgotPassword.verifyNonce(nonce);
        } catch (NonceException e) {
            prepareResponse(response, presenter, StatusCode.BAD_REQUEST, JSP_PATH_ERROR);
            return response;
        }

        response.setStatusCode(StatusCode.OK);
        response.setPresenter(Optional.of(presenter));
        response.setTemplate(Optional.of(JSP_FORM_PATH));

        return response;
    }

    @Override
    public Response<WebSiteSession> post(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        Map<String, List<String>> form = request.getFormData();
        Map<String, List<String>> params = request.getQueryParams();

        String password = getFormValue(form.get(PASSWORD));
        String repeatPassword = getFormValue(form.get(REPEAT_PASSWORD));
        String nonce = getFormValue(params.get(NONCE_URL_PARAM));

        try {
            forgotPassword.reset(nonce, password, repeatPassword);
        } catch (BadRequestException e) {
            String template = NONCE_URL_PARAM.equals(e.getField()) ? JSP_PATH_ERROR : JSP_FORM_PATH;
            UpdatePasswordPresenter presenter = makePresenterOnError(request.getCsrfChallenge().get(), e.getDescription());
            response.setPresenter(Optional.of(presenter));
            response.setTemplate(Optional.of(template));
            response.setStatusCode(StatusCode.BAD_REQUEST);
            return response;
        } catch (NotFoundException e) {
            UpdatePasswordPresenter presenter = presenter(request.getCsrfChallenge().get());
            response.setPresenter(Optional.of(presenter));
            response.setStatusCode(StatusCode.NOT_FOUND);
            response.setTemplate(Optional.of(JSP_EXPIRED_PATH));
            return response;
        }

        AssetPresenter presenter = new AssetPresenter(globalCssPath);
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.OK);
        response.setTemplate(Optional.of(JSP_OK_PATH));
        return response;
    }

    protected UpdatePasswordPresenter presenter(String csrfToken) {
        return new UpdatePasswordPresenter(
            globalCssPath, csrfToken, "/forgot-password"
        );
    }

    protected void prepareResponse(Response<WebSiteSession> response, UpdatePasswordPresenter presenter, StatusCode statusCode, String template) {
        response.setStatusCode(statusCode);
        response.setPresenter(Optional.of(presenter));
        response.setTemplate(Optional.of(template));
    }

    protected UpdatePasswordPresenter makePresenterOnError(String encodedCsrfToken, String errorMessage) {
        UpdatePasswordPresenter p = presenter(encodedCsrfToken);
        p.setErrorMessage(Optional.of(errorMessage));
        return p;
    }

    protected String getNonceUrlValue(List<String> values) throws InvalidParamException{
        if (values == null || values.size() == 0 || values.size() > 1) {
            throw new InvalidParamException(NONCE_URL_ERROR_MSG);
        }
        return values.get(0);
    }

    protected String getFormValue(List<String> formValue) {
        String value = null;
        if (formValue != null && formValue.size() == 1) {
            value = formValue.get(0);
        }
        return value;
    }
}
