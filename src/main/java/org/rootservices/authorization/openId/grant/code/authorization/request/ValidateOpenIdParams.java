package org.rootservices.authorization.openId.grant.code.authorization.request;

import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.code.authorization.request.entity.OpenIdAuthRequest;

import java.util.List;

/**
 * Created by tommackenzie on 10/10/15.
 */
public interface ValidateOpenIdParams {
    OpenIdAuthRequest run(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states) throws InformResourceOwnerException, InformClientException;
}
