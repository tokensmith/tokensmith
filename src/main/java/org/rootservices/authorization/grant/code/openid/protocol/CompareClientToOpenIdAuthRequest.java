package org.rootservices.authorization.grant.code.openid.protocol;

import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.openid.protocol.authorization.request.entity.OpenIdAuthRequest;

/**
 * Created by tommackenzie on 9/30/15.
 */
public interface CompareClientToOpenIdAuthRequest {
    boolean run(OpenIdAuthRequest authRequest) throws InformResourceOwnerException, InformClientException;
}
