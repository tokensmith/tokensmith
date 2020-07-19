package net.tokensmith.authorization.http.controller.resource.html;

import net.tokensmith.authorization.exception.BadRequestException;
import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import net.tokensmith.authorization.http.presenter.AssetPresenter;
import net.tokensmith.authorization.http.presenter.ForgotPasswordPresenter;
import net.tokensmith.authorization.nonce.reset.ForgotPassword;
import net.tokensmith.authorization.register.exception.NonceException;
import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class ForgotPasswordResource extends Resource<WebSiteSession, WebSiteUser> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForgotPasswordResource.class);
    public static String URL = "/forgot-password(.*)";

    private String globalCssPath;
    private ForgotPassword forgotPassword;

    private static String BLANK = "";
    private static String EMAIL = "email";
    private static String JSP_FORM_PATH = "/WEB-INF/jsp/password/forgot-password.jsp";
    private static String JSP_OK_PATH = "/WEB-INF/jsp/password/forgot-password-ok.jsp";

    public ForgotPasswordResource(String globalCssPath, ForgotPassword forgotPassword) {
        this.globalCssPath = globalCssPath;
        this.forgotPassword = forgotPassword;
    }

    @Override
    public Response<WebSiteSession> get(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {

        ForgotPasswordPresenter presenter = makePresenter(BLANK, request.getCsrfChallenge().get());
        response.setStatusCode(StatusCode.OK);
        response.setPresenter(Optional.of(presenter));
        response.setTemplate(Optional.of(JSP_FORM_PATH));

        return response;
    }

    @Override
    public Response<WebSiteSession> post(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        Map<String, List<String>> form = request.getFormData();

        String email = getFormValue(form.get(EMAIL));

        try {
            forgotPassword.sendMessage(email, baseURI(request));
        } catch (BadRequestException e) {
            ForgotPasswordPresenter presenter = makePresenterOnError(email, request.getCsrfChallenge().get(), e.getDescription());
            response.setPresenter(Optional.of(presenter));
            response.setStatusCode(StatusCode.BAD_REQUEST);
            response.setTemplate(Optional.of(JSP_FORM_PATH));
            return response;
        } catch (NonceException e) {
            // silently fail, might be attempting to harvest email addresses.
            LOGGER.info(e.getMessage(), e);
        }

        AssetPresenter presenter = new AssetPresenter(globalCssPath);
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.OK);
        response.setTemplate(Optional.of(JSP_OK_PATH));
        return response;
    }

    protected String baseURI(Request<WebSiteSession, WebSiteUser> request) {
        StringBuilder baseURI = new StringBuilder()
                .append(request.getScheme())
                .append("://")
                .append(request.getAuthority());

        if (Objects.nonNull(request.getPort()))
            baseURI = baseURI.append(":").append(request.getPort().toString());

        return baseURI.toString();
    }

    protected ForgotPasswordPresenter makePresenter(String email, String encodedCsrfToken) {
        var presenter = new ForgotPasswordPresenter(globalCssPath, email, encodedCsrfToken);
        presenter.setErrorMessage(Optional.empty());
        return presenter;
    }

    protected ForgotPasswordPresenter makePresenterOnError(String email, String encodedCsrfToken, String errorMessage) {
        ForgotPasswordPresenter p = new ForgotPasswordPresenter(globalCssPath, email, encodedCsrfToken);
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
