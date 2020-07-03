package net.tokensmith.authorization.http.controller.resource.html;


import net.tokensmith.authorization.http.controller.resource.html.authorization.claim.RedirectClaim;
import net.tokensmith.authorization.http.presenter.AssetPresenter;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.otter.security.cookie.CookieJwtException;
import net.tokensmith.otter.security.cookie.CookieSecurity;
import net.tokensmith.otter.security.cookie.either.ReadEither;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import net.tokensmith.authorization.http.presenter.RegisterPresenter;
import net.tokensmith.authorization.register.Register;
import net.tokensmith.authorization.register.RegisterError;
import net.tokensmith.authorization.register.exception.NonceException;
import net.tokensmith.authorization.register.exception.RegisterException;
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
    private CookieSecurity cookieSigner;
    private Register register;

    private static String JSP_PATH_FORM = "/WEB-INF/jsp/register/register.jsp";
    private static String JSP_PATH_OK = "/WEB-INF/jsp/register/register-ok.jsp";
    private static String EMAIL = "email";
    private static String PASSWORD = "password";
    private static String REPEAT_PASSWORD = "repeatPassword";
    private static String BLANK = "";

    @Autowired
    public RegisterResource(String globalCssPath, CookieSecurity cookieSigner, Register register) {
        this.globalCssPath = globalCssPath;
        this.cookieSigner = cookieSigner;
        this.register = register;
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

        if (Objects.nonNull(request.getCookies().get(CookieName.REDIRECT.toString()))) {
            prepareRedirectResponse(response, request.getCookies().get(CookieName.REDIRECT.toString()));
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

    /**
     * Will attempt to redirect the user the value of the redirect cookie.
     *
     * When there is an issue reading the cookie
     * Then it will not redirect the user and show the OK template
     * And it will remove the cookie
     *
     * When it can read the cookie
     * Then it will use it to set the location header
     * and set status code to 302
     * and set the cookie with done is true
     *
     * @param response the response
     * @param redirectCookie the redirect cookie
     */
    protected void prepareRedirectResponse(Response<WebSiteSession> response, Cookie redirectCookie) {
        ReadEither<RedirectClaim> signerEither = cookieSigner.read(redirectCookie.toString(), RedirectClaim.class);
        if (signerEither.getRight().isPresent()) {
            RedirectClaim redirectClaim = signerEither.getRight().get();
            response.setStatusCode(StatusCode.MOVED_TEMPORARILY);
            response.getHeaders().put(Header.LOCATION.toString(), redirectClaim.getRedirect());

            // reset to done is true.
            redirectClaim.setDone(true);
            CookieConfig config = new CookieConfig.Builder()
                    .build();

            try {
                Cookie redirectCookieDone = cookieSigner.make(config, redirectClaim);
                response.getCookies().put(CookieName.REDIRECT.toString(), redirectCookieDone);
            } catch (CookieJwtException e) {
                LOGGER.error("Could not make signed cookie");
                LOGGER.error(e.getMessage(), e);
                // 173 should it be removed?
            }

        } else {
            LOGGER.warn("Could not read redirect cookie");
            // 173: log the error from sign either left.
            response.getCookies().remove(CookieName.REDIRECT.toString());
            AssetPresenter presenter = new AssetPresenter(globalCssPath);
            prepareResponse(response, StatusCode.OK, presenter, JSP_PATH_OK);
        }
    }
}
