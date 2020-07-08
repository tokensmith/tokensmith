package helpers.fixture.persistence.http;

import io.netty.handler.codec.http.cookie.Cookie;

/**
 * Created by tommackenzie on 8/4/15.
 */
public class Session {
    private String csrfToken;
    private Cookie session;
    private Cookie redirect;
    private Cookie csrf;

    public String getCsrfToken() {
        return csrfToken;
    }

    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }

    public Cookie getSession() {
        return session;
    }

    public void setSession(Cookie session) {
        this.session = session;
    }

    public Cookie getCsrf() {
        return csrf;
    }

    public void setCsrf(Cookie csrf) {
        this.csrf = csrf;
    }

    public Cookie getRedirect() {
        return redirect;
    }

    public void setRedirect(Cookie redirect) {
        this.redirect = redirect;
    }
}
