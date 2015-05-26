package org.rootservices.authorization.authenticate;

import org.rootservices.authorization.authenticate.exception.UnauthorizedException;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/12/15.
 */
public interface LoginResourceOwner {
    UUID run(String userName, String plainTextPassword) throws UnauthorizedException;
}
