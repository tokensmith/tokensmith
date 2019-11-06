package net.tokensmith.authorization.oauth2.grant.password.entity;

import java.util.List;

/**
 * Created by tommackenzie on 9/18/16.
 */
public class TokenInputPasswordGrant {
    private String userName;
    private String password;
    private List<String> scopes;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }
}
