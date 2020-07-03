package net.tokensmith.authorization.http.presenter;

import java.util.Optional;

/**
 * Created by tommackenzie on 5/6/15.
 */
public class AuthorizationPresenter extends AssetPresenter {

    private Optional<String> userMessage;
    private String email;
    private String encodedCsrfToken;

    public Optional<String> getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(Optional<String> userMessage) {
        this.userMessage = userMessage;
    }

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
