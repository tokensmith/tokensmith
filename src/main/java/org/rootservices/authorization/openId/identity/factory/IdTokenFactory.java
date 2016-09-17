package org.rootservices.authorization.openId.identity.factory;

import org.rootservices.authorization.openId.identity.entity.Address;
import org.rootservices.authorization.openId.identity.entity.IdToken;
import org.rootservices.authorization.openId.identity.translator.AddrToAddrClaims;
import org.rootservices.authorization.openId.identity.translator.ProfileToIdToken;
import org.rootservices.authorization.persistence.entity.Profile;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.entity.TokenScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by tommackenzie on 3/19/16.
 * TODO: should this be a builder?
 */
@Component
public class IdTokenFactory {
    private static String PROFILE = "profile";
    private static String EMAIL = "email";
    private static String ADDR = "address";
    private static String PHONE = "phone";

    private ProfileToIdToken profileToIdToken;
    private AddrToAddrClaims addrToAddrClaims;

    @Autowired
    public IdTokenFactory(ProfileToIdToken profileToIdToken, AddrToAddrClaims addrToAddrClaims) {
        this.profileToIdToken = profileToIdToken;
        this.addrToAddrClaims = addrToAddrClaims;
    }

    public IdToken make(String accessTokenHash, String nonce, List<String> scopes, Profile profile) {
        IdToken idToken = make(scopes, profile);

        idToken.setAccessTokenHash(Optional.of(accessTokenHash));
        idToken.setNonce(Optional.of(nonce));

        return idToken;
    }

    public IdToken make(String nonce, List<String> scopes, Profile profile) {
        IdToken idToken = make(scopes, profile);
        idToken.setNonce(Optional.of(nonce));

        return idToken;
    }

    public IdToken make(List<String> scopes, Profile profile) {
        IdToken idToken = new IdToken();

        if (scopes.contains(PROFILE)) {
            profileToIdToken.toProfileClaims(idToken, profile);
        }

        if (scopes.contains(EMAIL)) {
            profileToIdToken.toEmailClaims(idToken, profile.getResourceOwner().getEmail(), profile.getResourceOwner().isEmailVerified());
        }

        if (scopes.contains(PHONE)) {
            profileToIdToken.toPhoneClaims(
                idToken,
                profile.getPhoneNumber(),
                profile.isPhoneNumberVerified()
            );
        }

        if (scopes.contains(ADDR) && profile.getAddresses().size() > 0) {
            Address address = addrToAddrClaims.to(profile.getAddresses().get(0));
            idToken.setAddress(Optional.of(address));
        } else {
            idToken.setAddress(Optional.empty());
        }

        return idToken;
    }
}
