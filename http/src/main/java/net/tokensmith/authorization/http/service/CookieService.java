package net.tokensmith.authorization.http.service;

import net.tokensmith.authorization.http.controller.resource.html.CookieName;
import net.tokensmith.authorization.http.controller.resource.html.authorization.claim.RedirectClaim;
import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import net.tokensmith.authorization.http.presenter.AuthorizationPresenter;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.otter.security.cookie.CookieJwtException;
import net.tokensmith.otter.security.cookie.CookieSecurity;
import net.tokensmith.otter.security.cookie.either.ReadEither;
import net.tokensmith.otter.security.cookie.either.ReadError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * Responsible for setting and reading cookies as well as updating
 * http response based on logic about cookies. Right now it is used
 * by /authorization and /register with specific methods for each.
 */
@Component
public class CookieService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CookieService.class);
    private CookieSecurity cookieSigner;
    private final CookieConfig redirectConfig;

    @Autowired
    public CookieService(CookieSecurity cookieSigner, CookieConfig redirectConfig) {
        this.cookieSigner = cookieSigner;
        this.redirectConfig = redirectConfig;
    }

    /**
     * Determines if a redirect cookie should be put on the response or
     * if it should be read, removed, and show the user a message that they
     * registered successfully.
     *
     * @param presenter the presenter to add the register success message
     * @param request the http request to look for the redirect cookie
     * @param response the http response to remove teh redirect cookie
     */
    public void manageRedirectForAuth(AuthorizationPresenter presenter, Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) {
        var redirectCookie = request.getCookies().get(CookieName.REDIRECT.toString());

        if (Objects.isNull(redirectCookie)) {
            addRedirectForAuth(request.getPathWithParams(), response);
        } else {
            readRedirectForAuth(redirectCookie, presenter, response);
        }
    }

    public void addRedirectForAuth(String path, Response<WebSiteSession> response) {
        RedirectClaim redirectClaims = new RedirectClaim();
        redirectClaims.setRedirect(path);
        redirectClaims.setDone(false);

        Cookie redirectCookie;
        try {
            redirectCookie = cookieSigner.make(redirectConfig, redirectClaims);
        } catch (CookieJwtException e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }

        response.getCookies().put(redirectCookie.getName(), redirectCookie);
    }


    public void readRedirectForAuth(Cookie redirectCookie, AuthorizationPresenter presenter, Response<WebSiteSession> response) {
        ReadEither<RedirectClaim> redirectEither = cookieSigner.read(redirectCookie.getValue(), RedirectClaim.class);
        if(redirectEither.getRight().isPresent() && redirectEither.getRight().get().getDone()) {
            presenter.setUserMessage(Optional.of("Thanks for registering. We have sent you an email to verify your email address. You can now login."));
        } else if (redirectEither.getLeft().isPresent() && Objects.nonNull(redirectEither.getLeft().get().getCause())) {
            ReadError<RedirectClaim> left = redirectEither.getLeft().get();
            LOGGER.warn("Removing redirect cookie. Error verifying signature, {}", left.getCookieError());
            LOGGER.warn(left.getCause().getMessage(), left.getCause());
            response.getCookies().remove(CookieName.REDIRECT.toString());
        } else if (redirectEither.getLeft().isPresent()){
            ReadError<RedirectClaim> left = redirectEither.getLeft().get();
            LOGGER.warn("Removing redirect cookie. Error verifying signature, {}. No cause was provided", left.getCookieError());
            response.getCookies().remove(CookieName.REDIRECT.toString());
        }
    }

    public boolean readRedirectForRegister(Response<WebSiteSession> response, Cookie redirectCookie) {
        boolean worked = true;
        ReadEither<RedirectClaim> signerEither = cookieSigner.read(redirectCookie.getValue(), RedirectClaim.class);
        if (signerEither.getRight().isPresent()) {
            RedirectClaim redirectClaim = signerEither.getRight().get();
            response.setStatusCode(StatusCode.MOVED_TEMPORARILY);
            response.getHeaders().put(Header.LOCATION.toString(), redirectClaim.getRedirect());

            // reset to done is true.
            redirectClaim.setDone(true);

            try {
                Cookie redirectCookieDone = cookieSigner.make(redirectConfig, redirectClaim);
                response.getCookies().put(CookieName.REDIRECT.toString(), redirectCookieDone);
            } catch (CookieJwtException e) {
                LOGGER.error("Could not make signed cookie");
                LOGGER.error(e.getMessage(), e);
                worked = false;
            }

        } else {
            LOGGER.warn("Could not read redirect cookie");
            signerEither.getLeft().ifPresent(e -> {
                LOGGER.warn("Read Error: {}", e.getCookieError());
                if (Objects.nonNull(e.getCause())) {
                    LOGGER.warn(e.getCause().getMessage(), e.getCause());
                }
            });

            worked = false;
        }
        return worked;
    }
}
