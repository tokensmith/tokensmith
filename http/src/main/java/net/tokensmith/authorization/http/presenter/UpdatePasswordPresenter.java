package net.tokensmith.authorization.http.presenter;

import java.util.Optional;

public class UpdatePasswordPresenter extends AssetPresenter {
    private Optional<String> errorMessage;
    private String encodedCsrfToken;
    private String forgotPasswordLink;

    public UpdatePasswordPresenter(String globalCssPath, String encodedCsrfToken) {
        super(globalCssPath);
        this.encodedCsrfToken = encodedCsrfToken;
    }

    public UpdatePasswordPresenter(String globalCssPath, String encodedCsrfToken, String forgotPasswordLink) {
        super(globalCssPath);
        this.encodedCsrfToken = encodedCsrfToken;
        this.forgotPasswordLink = forgotPasswordLink;
    }

    public String getForgotPasswordLink() {
        return forgotPasswordLink;
    }

    public void setForgotPasswordLink(String forgotPasswordLink) {
        this.forgotPasswordLink = forgotPasswordLink;
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
