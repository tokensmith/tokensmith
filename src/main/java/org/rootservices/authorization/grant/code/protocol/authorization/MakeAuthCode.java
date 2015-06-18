package org.rootservices.authorization.grant.code.protocol.authorization;

import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.entity.AuthCode;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/17/15.
 *
 * This is a creational pattern. Is it a factory?
 */
public interface MakeAuthCode {
    AuthCode run(AccessRequest accessRequest, String authorizationCode, int secondsToExpire);
}
