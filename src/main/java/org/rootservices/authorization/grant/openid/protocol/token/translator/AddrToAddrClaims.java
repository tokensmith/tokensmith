package org.rootservices.authorization.grant.openid.protocol.token.translator;

import org.rootservices.authorization.grant.openid.protocol.token.response.entity.Address;

/**
 * Created by tommackenzie on 3/18/16.
 */
public interface AddrToAddrClaims {
    Address to(org.rootservices.authorization.persistence.entity.Address profileAddress);
}
