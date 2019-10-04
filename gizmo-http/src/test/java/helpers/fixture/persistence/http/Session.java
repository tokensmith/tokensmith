package helpers.fixture.persistence.http;

import com.ning.http.client.cookie.Cookie;

/**
 * Created by tommackenzie on 8/4/15.
 */
public class Session {
    private String csrfToken;
    private Cookie session;

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
}
