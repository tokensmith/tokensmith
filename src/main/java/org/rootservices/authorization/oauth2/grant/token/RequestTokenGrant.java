package org.rootservices.authorization.oauth2.grant.token;

import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.oauth2.grant.token.exception.BadRequestException;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.oauth2.grant.token.exception.NotFoundException;

import java.util.Map;
import java.util.UUID;

/**
 * Created by tommackenzie on 9/21/16.
 *
 * TODO: should this be renamed?
 */
public interface RequestTokenGrant {
    TokenResponse request(UUID clientId, String clientPassword, Map<String, String> request) throws BadRequestException, NotFoundException, UnauthorizedException;
}
