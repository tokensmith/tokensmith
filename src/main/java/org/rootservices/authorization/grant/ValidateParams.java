package org.rootservices.authorization.grant;

import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.factory.exception.StateException;

import java.util.List;

/**
 * Created by tommackenzie on 2/24/15.
 */
public interface ValidateParams {
    public boolean run(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states) throws InformResourceOwnerException, InformClientException, StateException;
}
