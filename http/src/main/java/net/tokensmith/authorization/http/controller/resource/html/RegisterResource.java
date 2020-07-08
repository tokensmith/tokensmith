package net.tokensmith.authorization.http.controller.resource.html;


import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import net.tokensmith.authorization.http.presenter.AssetPresenter;
import net.tokensmith.authorization.http.presenter.RegisterPresenter;
import net.tokensmith.authorization.http.service.CookieService;
import net.tokensmith.authorization.register.Register;
import net.tokensmith.authorization.register.RegisterError;
import net.tokensmith.authorization.register.exception.NonceException;
import net.tokensmith.authorization.register.exception.RegisterException;
import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class RegisterResource extends Resource<WebSiteSession, WebSiteUser> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterResource.class);

    public static String URL = "/register(.*)";

    private String globalCssPath;
    private Register register;
    private CookieService cookieService;

    private static String JSP_PATH_FORM = "/WEB-INF/jsp/register/register.jsp";
    private static String JSP_PATH_OK = "/WEB-INF/jsp/register/register-ok.jsp";
    private static String EMAIL = "email";
    private static String PASSWORD = "password";
    private static String REPEAT_PASSWORD = "repeatPassword";
    private static String BLANK = "";

    @Autowired
    public RegisterResource(String globalCssPath, Register register, CookieService cookieService) {
        this.globalCssPath = globalCssPath;
        this.register = register;
        this.cookieService = cookieService;
    }

    @Override
    public Response<WebSiteSession> get(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        RegisterPresenter presenter = makeRegisterPresenter(BLANK, request.getCsrfChallenge().get());
        response.setStatusCode(StatusCode.OK);
        response.setPresenter(Optional.of(presenter));
        response.setTemplate(Optional.of(JSP_PATH_FORM));
        return response;
    }

    @Override
    public Response<WebSiteSession> post(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
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
            prepareResponse(response, StatusCode.OK, presenter, JSP_PATH_FORM);
            return response;
        } catch (NonceException e) {
            // fail silently, could not insert nonce into database.
            LOGGER.error(e.getMessage(), e);
        }

        Cookie redirectCookie = request.getCookies().get(CookieName.REDIRECT.toString());
        if (Objects.nonNull(redirectCookie)) {
            redirectCookie(redirectCookie, response);
        } else {
            AssetPresenter presenter = new AssetPresenter(globalCssPath);
            prepareResponse(response, StatusCode.OK, presenter, JSP_PATH_OK);
        }
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
        presenter.setGlobalCssPath(globalCssPath);
        presenter.setEmail(defaultEmail);
        presenter.setEncodedCsrfToken(csrfToken);
        presenter.setErrorMessage(Optional.empty());
        return presenter;
    }

    protected void prepareResponse(Response<WebSiteSession> response, StatusCode statusCode, AssetPresenter presenter, String template) {
        response.setStatusCode(statusCode);
        response.setPresenter(Optional.of(presenter));
        response.setTemplate(Optional.of(template));
    }

    protected void redirectCookie(Cookie redirectCookie, Response<WebSiteSession> response) {
        boolean worked = cookieService.readRedirectForRegister(response, redirectCookie);
        // 173: review keeping this logic here instead of cookie service
        // this is here instead of cookie service b/c I didn't want to pull in prepare response to
        // cookie service. maybe I should have so it could be unit tested better.
        if (Boolean.FALSE.equals(worked)) {
            response.getCookies().remove(CookieName.REDIRECT.toString());
            AssetPresenter presenter = new AssetPresenter(globalCssPath);
            prepareResponse(response, StatusCode.OK, presenter, JSP_PATH_OK);
        }
    }
}
