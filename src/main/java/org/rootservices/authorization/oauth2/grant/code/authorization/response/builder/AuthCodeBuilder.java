package org.rootservices.authorization.oauth2.grant.code.authorization.response.builder;

import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.entity.AuthCode;

/**
 * Created by tommackenzie on 4/17/15.
 *
 * This is a creational pattern. Is it a builder?
 */
public interface AuthCodeBuilder {
    AuthCode run(AccessRequest accessRequest, String authorizationCode, int secondsToExpire);
}
