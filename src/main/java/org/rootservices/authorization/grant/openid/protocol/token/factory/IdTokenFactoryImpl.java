package org.rootservices.authorization.grant.openid.protocol.token.factory;

import org.rootservices.authorization.grant.openid.protocol.token.response.entity.Address;
import org.rootservices.authorization.grant.openid.protocol.token.response.entity.IdToken;
import org.rootservices.authorization.grant.openid.protocol.token.translator.AddrToAddrClaims;
import org.rootservices.authorization.grant.openid.protocol.token.translator.ProfileToIdToken;
import org.rootservices.authorization.persistence.entity.AccessRequestScope;
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
    public IdToken make(List<AccessRequestScope> accessRequestScopes, Profile profile) {
        IdToken idToken = new IdToken();


        if (hasScope(accessRequestScopes, PROFILE)) {
            profileToIdToken.toProfileClaims(idToken, profile);
        }

        if (hasScope(accessRequestScopes, EMAIL)) {
            profileToIdToken.toEmailClaims(idToken, profile.getResourceOwner().getEmail(), profile.getResourceOwner().isEmailVerified());
        }

        if (hasScope(accessRequestScopes, PHONE)) {
            profileToIdToken.toPhoneClaims(
                idToken,
                profile.getPhoneNumber(),
                profile.isPhoneNumberVerified()
            );
        }

        if (hasScope(accessRequestScopes, ADDR) && profile.getAddresses().size() > 0) {
            Address address = addrToAddrClaims.to(profile.getAddresses().get(0));
            idToken.setAddress(Optional.of(address));
        } else {
            idToken.setAddress(Optional.empty());
        }

        return idToken;
    }

    protected Boolean hasScope(List<AccessRequestScope> accessRequestScopes, String scope) {
        for(AccessRequestScope ars: accessRequestScopes){
            if (scope.equals(ars.getScope().getName())) {
                return true;
            }
        }
        return false;
    }
}
