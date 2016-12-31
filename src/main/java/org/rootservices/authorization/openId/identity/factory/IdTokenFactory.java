package org.rootservices.authorization.openId.identity.factory;

import org.rootservices.authorization.oauth2.grant.token.entity.TokenClaims;
import org.rootservices.authorization.openId.identity.entity.Address;
import org.rootservices.authorization.openId.identity.entity.IdToken;
import org.rootservices.authorization.openId.identity.translator.AddrToAddrClaims;
import org.rootservices.authorization.openId.identity.translator.ProfileToIdToken;
import org.rootservices.authorization.persistence.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    // make for implicit grant
    public IdToken make(String accessTokenHash, String nonce, TokenClaims tokenClaims, List<String> scopes, ResourceOwner ro) {
        IdToken idToken = make(tokenClaims, scopes, ro);
        idToken.setNonce(Optional.of(nonce));
        idToken.setAccessTokenHash(Optional.of(accessTokenHash));

        return idToken;
    }

    // make for open id implicit identity only
    public IdToken make(String nonce, TokenClaims tokenClaims, List<String> scopes, ResourceOwner ro) {
        IdToken idToken = make(tokenClaims, scopes, ro);
        idToken.setNonce(Optional.of(nonce));

        return idToken;
    }

    public IdToken make(TokenClaims tokenClaims, List<String> scopes, ResourceOwner ro) {
        IdToken idToken = new IdToken();

        idToken.setIssuer(Optional.of(tokenClaims.getIssuer()));
        idToken.setAudience(tokenClaims.getAudience());
        idToken.setIssuedAt(Optional.of(tokenClaims.getIssuedAt()));
        idToken.setExpirationTime(Optional.of(tokenClaims.getExpirationTime()));
        idToken.setAuthenticationTime(tokenClaims.getAuthTime());

        if (shouldIncludeProfile(scopes, ro)) {
            profileToIdToken.toProfileClaims(idToken, ro.getProfile());
        }

        if (scopes.contains(EMAIL)) {
            profileToIdToken.toEmailClaims(idToken, ro.getEmail(), ro.isEmailVerified());
        }

        if (shouldIncludePhone(scopes, ro)) {
            profileToIdToken.toPhoneClaims(
                idToken,
                ro.getProfile().getPhoneNumber(),
                ro.getProfile().isPhoneNumberVerified()
            );
        }

        if (shouldIncludeAddress(scopes, ro)) {
            Address address = addrToAddrClaims.to(ro.getProfile().getAddresses().get(0));
            idToken.setAddress(Optional.of(address));
        } else {
            idToken.setAddress(Optional.empty());
        }

        return idToken;
    }

    protected Boolean shouldIncludeProfile(List<String> scopes, ResourceOwner ro) {
        return scopes.contains(PROFILE) && ro.getProfile() != null;
    }

    protected Boolean shouldIncludePhone(List<String> scopes, ResourceOwner ro) {
        return scopes.contains(PHONE) && ro.getProfile() != null;
    }

    protected Boolean shouldIncludeAddress(List<String> scopes, ResourceOwner ro) {
        return scopes.contains(ADDR) && ro.getProfile() != null && ro.getProfile().getAddresses() != null && ro.getProfile().getAddresses().size() > 0;
    }
}
