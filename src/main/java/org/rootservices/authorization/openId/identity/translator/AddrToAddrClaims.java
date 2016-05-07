package org.rootservices.authorization.openId.identity.translator;

import org.rootservices.authorization.openId.identity.entity.Address;

/**
 * Created by tommackenzie on 3/18/16.
 */
public interface AddrToAddrClaims {
    Address to(org.rootservices.authorization.persistence.entity.Address profileAddress);
}
