package org.rootservices.authorization.authenticate;

import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.persistence.entity.ResourceOwner;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/12/15.
 */
public interface LoginResourceOwner {
    ResourceOwner run(String userName, String plainTextPassword) throws UnauthorizedException;
}
