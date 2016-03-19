package org.rootservices.authorization.grant.openid.protocol.token.translator;

import org.rootservices.authorization.grant.openid.protocol.token.response.entity.*;
import org.rootservices.authorization.persistence.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 3/17/16.
 *
 * http://openid.net/specs/openid-connect-core-1_0.html#ScopeClaims
 */
@Component
public class ProfileToIdTokenImpl implements ProfileToIdToken {

    public void toProfileClaims(IdToken idToken, Profile profile){

        // TODO: family_name can be many just like given names.

        // given_name,
        idToken.setFirstName(makeGivenNamesClaim(profile.getGivenNames()));

        // middle_name,
        idToken.setMiddleName(profile.getMiddleName());

        // nickname,
        idToken.setNickName(profile.getNickName());

        // preferred_username,
        idToken.setPreferredUsername(profile.getPreferredUserName());

        // profile,
        idToken.setProfile(profile.getProfile());

        // picture,
        idToken.setPicture(profile.getPicture());

        // website,
        idToken.setWebsite(profile.getWebsite());

        // gender
        idToken.setGender(makeGenderClaim(profile.getGender()));

        // birthdate,
        idToken.setBirthdate(profile.getBirthDate());

        // zoneinfo,
        idToken.setZoneInfo(profile.getZoneInfo());

        // locale,
        idToken.setLocale(profile.getLocale());

        // updated_at.
        Optional<Long> updatedAt = Optional.of(profile.getUpdatedAt().toEpochSecond());
        idToken.setUpdatedAt(updatedAt);
    }

    public void toEmailClaims(IdToken idToken, String email, Boolean isVerified) {
        idToken.setEmail(Optional.of(email));
        idToken.setEmailVerified(Optional.of(isVerified));
    }

    public void toPhoneClaims(IdToken idToken, Optional<String> phone, Boolean isVerified) {
        idToken.setPhoneNumber(phone);
        idToken.setPhoneNumberVerified(Optional.of(isVerified));
    }

    public Optional<String> makeGivenNamesClaim(List<GivenName> givenNames) {
        String names = null;
        for(GivenName givenName: givenNames) {
            if (names == null) {
                names = givenName.getName();
            } else {
                names += " " + givenName.getName();
            }
        }

        Optional<String> namesClaim = Optional.empty();
        if (names != null) {
            namesClaim = Optional.of(names);
        }
        return namesClaim;
    }

    public Optional<String> makeGenderClaim(Optional<Gender> profileGender){
        String gender = null;
        if (profileGender.isPresent()) {
            gender = profileGender.get().toString();
        }

        Optional<String> genderClaim = Optional.empty();
        if (gender != null) {
            genderClaim = Optional.of(gender);
        }
        return genderClaim;
    }
}
