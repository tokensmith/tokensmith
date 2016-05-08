package org.rootservices.authorization.openId.identity.translator;

import org.rootservices.authorization.openId.identity.entity.IdToken;
import org.rootservices.authorization.persistence.entity.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 3/17/16.
 */
public interface ProfileToIdToken {
    void toProfileClaims(IdToken idToken, Profile profile);
    void toEmailClaims(IdToken idToken, String email, Boolean isVerified);
    void toPhoneClaims(IdToken idToken, Optional<String> phone, Boolean isVerified);
    Optional<String> makeFamiyNamesClaim(List<FamilyName> familyNames);
    Optional<String> makeGivenNamesClaim(List<GivenName> givenNames);
    Optional<String> makeGenderClaim(Optional<Gender> profileGender);

}
