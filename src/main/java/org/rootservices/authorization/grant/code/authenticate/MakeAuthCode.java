package org.rootservices.authorization.grant.code.authenticate;

import org.rootservices.authorization.persistence.entity.AuthCode;

import java.text.ParseException;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/17/15.
 *
 * This is a creational pattern. Is it a factory?
 */
public interface MakeAuthCode {
    AuthCode run(UUID resourceOwnerUUID, UUID clientUUID, String authorizationCode, int secondsToExpire);
}
