package org.rootservices.authorization.openId.grant.token.request;

import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.token.request.entity.OpenIdTokenAuthRequest;

import java.util.List;

/**
 * Created by tommackenzie on 7/23/16.
 */
public class ValidateOpenIdTokenParams {

    public OpenIdTokenAuthRequest validate(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states, List<String> nonce) throws InformResourceOwnerException, InformClientException {
        return null;
    }
}
