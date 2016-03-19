package org.rootservices.authorization.grant.openid.protocol.token.translator;

import org.rootservices.authorization.grant.openid.protocol.token.response.entity.IdToken;
import org.rootservices.authorization.persistence.entity.Gender;
import org.rootservices.authorization.persistence.entity.GivenName;
import org.rootservices.authorization.persistence.entity.Profile;
import org.rootservices.authorization.persistence.entity.Scope;

import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 3/17/16.
 */
public interface ProfileToIdToken {
    void toProfileClaims(IdToken idToken, Profile profile);
    void toEmailClaims(IdToken idToken, String email, Boolean isVerified);
    void toPhoneClaims(IdToken idToken, Optional<String> phone, Boolean isVerified);
    Optional<String> makeGivenNamesClaim(List<GivenName> givenNames);
    Optional<String> makeGenderClaim(Optional<Gender> profileGender);

}
