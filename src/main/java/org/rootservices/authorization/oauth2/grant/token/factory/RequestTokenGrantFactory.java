package org.rootservices.authorization.oauth2.grant.token.factory;

import org.rootservices.authorization.oauth2.grant.redirect.code.token.RequestTokenCodeGrant;
import org.rootservices.authorization.oauth2.grant.token.RequestTokenGrant;
import org.rootservices.authorization.oauth2.grant.password.RequestTokenPasswordGrant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 9/21/16.
 */
@Component
public class RequestTokenGrantFactory {
    private static String PASSWORD = "password";
    private static String CODE = "code";
    private RequestTokenPasswordGrant requestTokenPasswordGrant;
    private RequestTokenCodeGrant requestTokenCodeGrant;

    @Autowired
    public RequestTokenGrantFactory(RequestTokenPasswordGrant requestTokenPasswordGrant, RequestTokenCodeGrant requestTokenCodeGrant) {
        this.requestTokenPasswordGrant = requestTokenPasswordGrant;
        this.requestTokenCodeGrant = requestTokenCodeGrant;
    }

    public RequestTokenGrant make(String grantType) {
        RequestTokenGrant requestTokenGrant = null;
        if (PASSWORD.equals(grantType)) {
            requestTokenGrant = requestTokenPasswordGrant;
        } else if (CODE.equals(grantType)) {
            requestTokenGrant = requestTokenCodeGrant;
        }

        return requestTokenGrant;
    }
}
