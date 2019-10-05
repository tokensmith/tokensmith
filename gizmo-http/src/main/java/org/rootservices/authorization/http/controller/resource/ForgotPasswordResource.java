package org.rootservices.authorization.http.controller.resource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.exception.BadRequestException;
import org.rootservices.authorization.http.controller.security.TokenSession;
import org.rootservices.authorization.http.controller.security.WebSiteUser;
import org.rootservices.authorization.http.presenter.ForgotPasswordPresenter;
import org.rootservices.authorization.nonce.reset.ForgotPassword;
import org.rootservices.authorization.register.exception.NonceException;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ForgotPasswordResource extends Resource<TokenSession, WebSiteUser> {
    private static final Logger logger = LogManager.getLogger(ForgotPasswordResource.class);
    public static String URL = "/forgot-password(.*)";

    private ForgotPassword forgotPassword;

    private static String BLANK = "";
    private static String EMAIL = "email";
    private static String JSP_FORM_PATH = "/WEB-INF/jsp/password/forgot-password.jsp";
    private static String JSP_OK_PATH = "/WEB-INF/jsp/password/forgot-password-ok.jsp";

    public ForgotPasswordResource(ForgotPassword forgotPassword) {
        this.forgotPassword = forgotPassword;
    }

    @Override
    public Response<TokenSession> get(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {

        ForgotPasswordPresenter presenter = makePresenter(BLANK, request.getCsrfChallenge().get());
        response.setStatusCode(StatusCode.OK);
        response.setPresenter(Optional.of(presenter));
        response.setTemplate(Optional.of(JSP_FORM_PATH));

        return response;
    }

    @Override
    public Response<TokenSession> post(Request<TokenSession, WebSiteUser> request, Response<TokenSession> response) {
        Map<String, List<String>> form = request.getFormData();

        String email = getFormValue(form.get(EMAIL));

        try {
            forgotPassword.sendMessage(email);
        } catch (BadRequestException e) {
            ForgotPasswordPresenter presenter = makePresenterOnError(email, request.getCsrfChallenge().get(), e.getDescription());
            response.setPresenter(Optional.of(presenter));
            response.setStatusCode(StatusCode.BAD_REQUEST);
            response.setTemplate(Optional.of(JSP_FORM_PATH));
            return response;
        } catch (NonceException e) {
            // silently fail, might be attempting to harvest email addresses.
            logger.info(e.getMessage(), e);
        }

        response.setStatusCode(StatusCode.OK);
        response.setTemplate(Optional.of(JSP_OK_PATH));
        return response;
    }

    protected ForgotPasswordPresenter makePresenter(String email, String encodedCsrfToken) {
        return new ForgotPasswordPresenter(email, encodedCsrfToken);
    }

    protected ForgotPasswordPresenter makePresenterOnError(String email, String encodedCsrfToken, String errorMessage) {
        ForgotPasswordPresenter p = new ForgotPasswordPresenter(email, encodedCsrfToken);
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
