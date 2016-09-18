package org.rootservices.authorization.oauth2.grant.password;

import org.rootservices.authorization.oauth2.grant.password.entity.TokenInputPasswordGrant;
import org.rootservices.authorization.oauth2.grant.password.factory.TokenInputPasswordGrantFactory;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.response.TokenResponse;

import java.util.Map;

/**
 * Created by tommackenzie on 9/18/16.
 */
public class RequestTokenPasswordGrant {
    private TokenInputPasswordGrantFactory tokenInputPasswordGrantFactory;


    public TokenResponse request(Map<String, String> request) {
        // TokenInputPasswordGrant input = tokenInputPasswordGrantFactory.run(request);
        TokenResponse response = new TokenResponse();
        return response;
    }
}
