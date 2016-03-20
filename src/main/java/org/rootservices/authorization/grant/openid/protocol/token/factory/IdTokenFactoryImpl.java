package org.rootservices.authorization.grant.openid.protocol.token.factory;

import org.rootservices.authorization.grant.openid.protocol.token.response.entity.Address;
import org.rootservices.authorization.grant.openid.protocol.token.response.entity.IdToken;
import org.rootservices.authorization.grant.openid.protocol.token.translator.AddrToAddrClaims;
import org.rootservices.authorization.grant.openid.protocol.token.translator.ProfileToIdToken;
import org.rootservices.authorization.persistence.entity.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 3/19/16.
 */
@Component
public class IdTokenFactoryImpl implements IdTokenFactory {
    private static String PROFILE = "profile";
    private static String EMAIL = "email";
    private static String ADDR = "address";
    private static String PHONE = "phone";

    private ProfileToIdToken profileToIdToken;
    private AddrToAddrClaims addrToAddrClaims;

    @Autowired
    public IdTokenFactoryImpl(ProfileToIdToken profileToIdToken, AddrToAddrClaims addrToAddrClaims) {
        this.profileToIdToken = profileToIdToken;
        this.addrToAddrClaims = addrToAddrClaims;
    }

    @Override
    public IdToken make(List<String> claimRequest, String email, Boolean emailVerified, Profile profile) {
        IdToken idToken = new IdToken();

        if (claimRequest.contains(PROFILE)) {
            profileToIdToken.toProfileClaims(idToken, profile);
        }

        if (claimRequest.contains(EMAIL)) {
            profileToIdToken.toEmailClaims(idToken, email, emailVerified);
        }

        if (claimRequest.contains(PHONE)) {
            profileToIdToken.toPhoneClaims(
                idToken,
                profile.getPhoneNumber(),
                profile.isPhoneNumberVerified()
            );
        }

        if (claimRequest.contains(ADDR) && profile.getAddresses().size() > 0) {
            Address address = addrToAddrClaims.to(profile.getAddresses().get(0));
            idToken.setAddress(Optional.of(address));
        } else {
            idToken.setAddress(Optional.empty());
        }

        return idToken;
    }
}
