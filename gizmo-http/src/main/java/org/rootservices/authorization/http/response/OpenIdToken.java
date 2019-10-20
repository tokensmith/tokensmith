package org.rootservices.authorization.http.response;


/**
 * Created by tommackenzie on 2/20/16.
 */
public class OpenIdToken extends Token {
    private String idToken;

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
