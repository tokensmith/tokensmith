package org.rootservices.authorization.http.presenter;

import java.util.Optional;

public class UpdatePasswordPresenter {
    private Optional<String> errorMessage;
    private String encodedCsrfToken;

    public UpdatePasswordPresenter(String encodedCsrfToken) {
        this.encodedCsrfToken = encodedCsrfToken;
    }

    public String getEncodedCsrfToken() {
        return encodedCsrfToken;
    }

    public void setEncodedCsrfToken(String encodedCsrfToken) {
        this.encodedCsrfToken = encodedCsrfToken;
    }

    public Optional<String> getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(Optional<String> errorMessage) {
        this.errorMessage = errorMessage;
    }
}
