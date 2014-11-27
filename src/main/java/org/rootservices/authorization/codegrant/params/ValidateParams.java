package org.rootservices.authorization.codegrant.params;

import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;

import java.util.List;

/**
 * Created by tommackenzie on 11/27/14.
 */
public interface ValidateParams {
    public boolean run(List<String> clientIds, List<String> responseTypes) throws InformResourceOwnerException, InformClientException;
}
