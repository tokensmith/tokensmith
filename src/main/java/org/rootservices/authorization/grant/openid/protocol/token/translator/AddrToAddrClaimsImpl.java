package org.rootservices.authorization.grant.openid.protocol.token.translator;

import org.rootservices.authorization.grant.openid.protocol.token.response.entity.Address;
import org.springframework.stereotype.Component;


/**
 * Created by tommackenzie on 3/18/16.
 * http://openid.net/specs/openid-connect-core-1_0.html#ScopeClaims
 */
@Component
public class AddrToAddrClaimsImpl implements AddrToAddrClaims {

    @Override
    public Address to(org.rootservices.authorization.persistence.entity.Address profileAddress) {

        Address address = new Address();

        String streetAddress = profileAddress.getStreetAddress();
        if (profileAddress.getStreetAddress2().isPresent()) {
            streetAddress += " " + profileAddress.getStreetAddress2();
        }

        address.setStreetAddress(streetAddress);
        address.setLocality(profileAddress.getLocality());
        address.setRegion(profileAddress.getRegion());
        address.setPostalCode(profileAddress.getPostalCode());
        address.setCountry(profileAddress.getCountry());

        address.setFormatted(
                address.getStreetAddress() + "\n" +
                address.getLocality() + ", " +
                address.getRegion() + " " +
                address.getPostalCode() + "\n" +
                address.getCountry()
        );
        return address;
    }
}
