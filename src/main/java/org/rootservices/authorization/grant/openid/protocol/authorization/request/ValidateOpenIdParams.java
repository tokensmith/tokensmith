package org.rootservices.authorization.grant.openid.protocol.authorization.request;

import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.openid.protocol.authorization.request.entity.OpenIdAuthRequest;

import java.util.List;

/**
 * Created by tommackenzie on 10/10/15.
 */
public interface ValidateOpenIdParams {
    OpenIdAuthRequest run(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states) throws InformResourceOwnerException, InformClientException;
}
