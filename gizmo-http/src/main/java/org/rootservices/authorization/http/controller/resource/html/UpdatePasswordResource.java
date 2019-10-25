package org.rootservices.authorization.http.controller.resource.html;

import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.exception.BadRequestException;
import org.rootservices.authorization.exception.NotFoundException;
import org.rootservices.authorization.http.controller.security.TokenSession;
import org.rootservices.authorization.http.controller.security.WebSiteUser;
import org.rootservices.authorization.http.presenter.UpdatePasswordPresenter;
import org.rootservices.authorization.nonce.reset.ForgotPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Component
public class UpdatePasswordResource extends Resource<TokenSession, WebSiteUser> {
    private static final Logger logger = LogManager.getLogger(UpdatePasswordResource.class);
    public static String URL = "/update-password\\?nonce=(.*)";

    private ForgotPassword forgotPassword;

    private static String NONCE_URL_PARAM = "nonce";
    private static String PASSWORD = "password";
    private static String REPEAT_PASSWORD = "repeatPassword";

    private static String JSP_FORM_PATH = "/WEB-INF/jsp/password/update-password.jsp";
    private static String JSP_OK_PATH = "/WEB-INF/jsp/password/update-password-ok.jsp";
    private static String JSP_EXPIRED_PATH = "/WEB-INF/jsp/password/update-password-expired.jsp";

    @Autowired
    public UpdatePasswordResource(ForgotPassword forgotPassword) {
        this.forgotPassword = forgotPassword;
    }

    @Override
    public Response<TokenSession> get(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {

        UpdatePasswordPresenter presenter = new UpdatePasswordPresenter(request.getCsrfChallenge().get());
        response.setStatusCode(StatusCode.OK);
        response.setPresenter(Optional.of(presenter));
        response.setTemplate(Optional.of(JSP_FORM_PATH));

        return response;
    }

    @Override
    public Response<TokenSession> post(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        Map<String, List<String>> form = request.getFormData();
        Map<String, List<String>> params = request.getQueryParams();

        String password = getFormValue(form.get(PASSWORD));
        String repeatPassword = getFormValue(form.get(REPEAT_PASSWORD));
        String nonce = getFormValue(params.get(NONCE_URL_PARAM));

        try {
            forgotPassword.reset(nonce, password, repeatPassword);
        } catch (BadRequestException e) {
            UpdatePasswordPresenter presenter = makePresenterOnError(request.getCsrfChallenge().get(), e.getDescription());
            response.setPresenter(Optional.of(presenter));
            response.setTemplate(Optional.of(JSP_FORM_PATH));
            response.setStatusCode(StatusCode.BAD_REQUEST);
            return response;
        } catch (NotFoundException e) {
            response.setStatusCode(StatusCode.NOT_FOUND);
            response.setTemplate(Optional.of(JSP_EXPIRED_PATH));
            return response;
        }

        response.setStatusCode(StatusCode.OK);
        response.setTemplate(Optional.of(JSP_OK_PATH));
        return response;
    }

    protected UpdatePasswordPresenter makePresenterOnError(String encodedCsrfToken, String errorMessage) {
        UpdatePasswordPresenter p = new UpdatePasswordPresenter(encodedCsrfToken);
        p.setErrorMessage(Optional.of(errorMessage));
        return p;
    }

    protected String getFormValue(List<String> formValue) {
        String value = null;
        if (formValue != null && formValue.size() == 1) {
            value = formValue.get(0);
        }
        return value;
    }
}
