package org.rootservices.authorization.oauth2.grant.redirect.authorization.request;

import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.AuthRequestBuilder;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.entity.AuthRequest;

import java.util.List;

/**
 * Created by tommackenzie on 2/24/15.
 */
public interface ValidateParams {
    AuthRequest run(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states) throws InformResourceOwnerException, InformClientException;
}
