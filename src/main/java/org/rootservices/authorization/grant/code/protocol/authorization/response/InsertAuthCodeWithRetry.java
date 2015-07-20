package org.rootservices.authorization.grant.code.protocol.authorization.response;

import org.rootservices.authorization.grant.code.protocol.authorization.exception.AuthCodeInsertException;
import org.rootservices.authorization.persistence.entity.AccessRequest;

/**
 * Created by tommackenzie on 7/16/15.
 */
public interface InsertAuthCodeWithRetry {
    String run(AccessRequest accessRequest, int attempt) throws AuthCodeInsertException;
    int getSecondsToExpiration();
}
