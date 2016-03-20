package org.rootservices.authorization.grant.openid.protocol.token.factory;

import org.rootservices.authorization.grant.openid.protocol.token.response.entity.IdToken;
import org.rootservices.authorization.persistence.entity.Profile;

import java.util.List;

/**
 * Created by tommackenzie on 3/19/16.
 */
public interface IdTokenFactory {
    IdToken make(List<String> claimRequest, String email, Boolean emailVerified, Profile profile);
}
