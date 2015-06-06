package org.rootservices.authorization.authenticate;

import org.rootservices.authorization.authenticate.exception.UnauthorizedException;

/**
 * Created by tommackenzie on 5/25/15.
 */
public interface MatchPasswords {
    boolean run(String plainTextPassword, byte[] hashedPassword) throws UnauthorizedException;
}
