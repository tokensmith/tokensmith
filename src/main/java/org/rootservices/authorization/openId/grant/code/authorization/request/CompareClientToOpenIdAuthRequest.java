package org.rootservices.authorization.openId.grant.code.authorization.request;

import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.code.authorization.request.entity.OpenIdAuthRequest;

/**
 * Created by tommackenzie on 9/30/15.
 */
public interface CompareClientToOpenIdAuthRequest {
    boolean run(OpenIdAuthRequest authRequest) throws InformResourceOwnerException, InformClientException;
}
