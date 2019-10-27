package net.tokensmith.authorization.http.presenter;

/**
 * Created by tommackenzie on 5/6/15.
 */
public class AuthorizationPresenter {

    private String email;
    private String encodedCsrfToken;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getEncodedCsrfToken() {
        return encodedCsrfToken;
    }

    public void setEncodedCsrfToken(String encodedCsrfToken) {
        this.encodedCsrfToken = encodedCsrfToken;
    }
}
