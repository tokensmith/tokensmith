package net.tokensmith.authorization.oauth2.grant.refresh.entity;

import java.util.List;

/**
 * Created by tommackenzie on 10/7/16.
 */
public class TokenInputRefreshGrant {
    private String refreshToken;
    private List<String> scopes;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }
}
