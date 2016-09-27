package org.rootservices.authorization.oauth2.grant.foo.factory;

import org.rootservices.authorization.oauth2.grant.foo.RequestTokenGrant;
import org.rootservices.authorization.oauth2.grant.password.RequestTokenPasswordGrant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 9/21/16.
 */
@Component
public class RequestTokenFactory {
    private static String PASSWORD = "password";
    private RequestTokenPasswordGrant requestTokenPasswordGrant;

    @Autowired
    public RequestTokenFactory(RequestTokenPasswordGrant requestTokenPasswordGrant) {
        this.requestTokenPasswordGrant = requestTokenPasswordGrant;
    }

    public RequestTokenGrant make(String grantType) {
        RequestTokenGrant requestToken = null;
        if (PASSWORD.equals(grantType)) {
            requestToken = requestTokenPasswordGrant;
        }

        return requestToken;
    }
}
