package net.tokensmith.authorization.http.controller.resource.html;

import net.tokensmith.authorization.http.presenter.AssetPresenter;
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

    private String globalCssPath;
    private ForgotPassword forgotPassword;

    private static String NONCE_URL_PARAM = "nonce";
    private static String PASSWORD = "password";
    private static String REPEAT_PASSWORD = "repeatPassword";

    private static String JSP_FORM_PATH = "/WEB-INF/jsp/password/update-password.jsp";
    private static String JSP_OK_PATH = "/WEB-INF/jsp/password/update-password-ok.jsp";
    private static String JSP_EXPIRED_PATH = "/WEB-INF/jsp/password/update-password-expired.jsp";

    @Autowired
    public UpdatePasswordResource(String globalCssPath, ForgotPassword forgotPassword) {
        this.globalCssPath = globalCssPath;
        this.forgotPassword = forgotPassword;
    }

    @Override
    public Response<WebSiteSession> get(Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {

        UpdatePasswordPresenter presenter = new UpdatePasswordPresenter(
            globalCssPath,
            request.getCsrfChallenge().get()
        );
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
            UpdatePasswordPresenter presenter = makePresenterOnError(request.getCsrfChallenge().get(), e.getDescription());
            response.setPresenter(Optional.of(presenter));
            response.setTemplate(Optional.of(JSP_FORM_PATH));
            response.setStatusCode(StatusCode.BAD_REQUEST);
            return response;
        } catch (NotFoundException e) {
            AssetPresenter presenter = new AssetPresenter(globalCssPath);
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

    protected UpdatePasswordPresenter makePresenterOnError(String encodedCsrfToken, String errorMessage) {
        UpdatePasswordPresenter p = new UpdatePasswordPresenter(globalCssPath, encodedCsrfToken);
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
