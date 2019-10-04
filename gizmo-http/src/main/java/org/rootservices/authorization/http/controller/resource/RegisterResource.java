package org.rootservices.authorization.http.controller.resource;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.http.controller.resource.authorization.openid.OpenIdCodeResource;
import org.rootservices.authorization.http.presenter.RegisterPresenter;
import org.rootservices.authorization.register.Register;
import org.rootservices.authorization.register.RegisterError;
import org.rootservices.authorization.register.exception.NonceException;
import org.rootservices.authorization.register.exception.RegisterException;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class RegisterResource extends Resource {
    private static final Logger logger = LogManager.getLogger(RegisterResource.class);

    public static String URL = "/register(.*)";
    private Register register;
    private static String JSP_PATH = "/WEB-INF/jsp/register.jsp";
    private static String EMAIL = "email";
    private static String PASSWORD = "password";
    private static String REPEAT_PASSWORD = "repeatPassword";
    private static String BLANK = "";

    @Autowired
    public RegisterResource(Register register) {
        this.register = register;
    }

    @Override
    public Response get(Request request, Response response) {
        RegisterPresenter presenter = makeRegisterPresenter(BLANK, request.getCsrfChallenge().get());
        response.setStatusCode(StatusCode.OK);
        response.setPresenter(Optional.of(presenter));
        response.setTemplate(Optional.of(JSP_PATH));
        return response;
    }

    @Override
    public Response post(Request request, Response response) {
        Map<String, List<String>> form = request.getFormData();

        String email = getFormValue(form.get(EMAIL));

        try {
            register.run(
                email,
                getFormValue(form.get(PASSWORD)),
                getFormValue(form.get(REPEAT_PASSWORD))
            );
        } catch (RegisterException e) {
            String errorMessage = errorMessage(e.getRegisterError());
            RegisterPresenter presenter = makeRegisterPresenterOnError(email, request.getCsrfChallenge().get(), errorMessage);
            prepareResponse(response, StatusCode.OK, presenter);
            return response;
        } catch (NonceException e) {
            // fail silently, could not insert nonce into database.
            logger.error(e.getMessage(), e);
        }

        RegisterPresenter presenter = makeRegisterPresenter(BLANK, request.getCsrfChallenge().get());
        prepareResponse(response, StatusCode.OK, presenter);
        return response;
    }

    protected String getFormValue(List<String> formValue) {
        String value = null;
        if (formValue != null && formValue.size() == 1) {
            value = formValue.get(0);
        }
        return value;
    }

    protected String errorMessage(RegisterError registerError) {
        String errorMessage = "Sorry please try again";
        if (registerError == RegisterError.EMAIL_MISSING) {
            errorMessage = "Email is required";
        } else if (registerError == RegisterError.PASSWORD_MISSING) {
            errorMessage = "Password is required";
        } else if (registerError == RegisterError.REPEAT_PASSWORD_MISSING) {
            errorMessage = "Repeat password is required";
        } else if (registerError == RegisterError.EMAIL_TAKEN) {
            errorMessage = "Please use a different email address";
        } else if (registerError == RegisterError.PASSWORD_MISMATCH) {
            errorMessage = "Passwords must match.";
        }
        return errorMessage;
    }

    protected RegisterPresenter makeRegisterPresenterOnError(String defaultEmail, String csrfToken, String errorMessage) {
        RegisterPresenter presenter = makeRegisterPresenter(defaultEmail, csrfToken);
        presenter.setErrorMessage(Optional.of(errorMessage));
        return presenter;
    }

    protected RegisterPresenter makeRegisterPresenter(String defaultEmail, String csrfToken) {
        RegisterPresenter presenter = new RegisterPresenter();
        presenter.setEmail(defaultEmail);
        presenter.setEncodedCsrfToken(csrfToken);
        return presenter;
    }

    protected void prepareResponse(Response response, StatusCode statusCode, RegisterPresenter presenter) {
        response.setStatusCode(statusCode);
        response.setPresenter(Optional.of(presenter));
        response.setTemplate(Optional.of(JSP_PATH));
    }
}
