package net.tokensmith.authorization.oauth2.grant.token.factory;

import net.tokensmith.authorization.oauth2.grant.password.RequestTokenPasswordGrant;
import net.tokensmith.authorization.oauth2.grant.redirect.code.token.RequestTokenCodeGrant;
import net.tokensmith.authorization.oauth2.grant.refresh.RequestTokenRefreshGrant;
import net.tokensmith.authorization.oauth2.grant.token.RequestTokenGrant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 9/21/16.
 */
@Component
public class RequestTokenGrantFactory {
    private static String PASSWORD = "password";
    private static String CODE = "authorization_code";
    private static String REFRESH = "refresh_token";
    private RequestTokenPasswordGrant requestTokenPasswordGrant;
    private RequestTokenCodeGrant requestTokenCodeGrant;
    private RequestTokenRefreshGrant requestTokenRefreshGrant;

    @Autowired
    public RequestTokenGrantFactory(RequestTokenPasswordGrant requestTokenPasswordGrant, RequestTokenCodeGrant requestTokenCodeGrant, RequestTokenRefreshGrant requestTokenRefreshGrant) {
        this.requestTokenPasswordGrant = requestTokenPasswordGrant;
        this.requestTokenCodeGrant = requestTokenCodeGrant;
        this.requestTokenRefreshGrant = requestTokenRefreshGrant;
    }

    public RequestTokenGrant make(String grantType) {
        RequestTokenGrant requestTokenGrant = null;
        if (PASSWORD.equals(grantType)) {
            requestTokenGrant = requestTokenPasswordGrant;
        } else if (CODE.equals(grantType)) {
            requestTokenGrant = requestTokenCodeGrant;
        } else if (REFRESH.equals(grantType)) {
            requestTokenGrant = requestTokenRefreshGrant;
        }

        return requestTokenGrant;
    }
}
