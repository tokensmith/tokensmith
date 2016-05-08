package org.rootservices.authorization.openId.identity.translator;

import org.rootservices.authorization.openId.identity.entity.*;
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

    @Override
    public void toProfileClaims(IdToken idToken, Profile profile){

        idToken.setLastName(makeFamiyNamesClaim(profile.getFamilyNames()));
        idToken.setFirstName(makeGivenNamesClaim(profile.getGivenNames()));
        idToken.setMiddleName(profile.getMiddleName());
        idToken.setNickName(profile.getNickName());
        idToken.setPreferredUsername(profile.getPreferredUserName());
        idToken.setProfile(profile.getProfile());
        idToken.setPicture(profile.getPicture());
        idToken.setWebsite(profile.getWebsite());
        idToken.setGender(makeGenderClaim(profile.getGender()));
        idToken.setBirthdate(profile.getBirthDate());
        idToken.setZoneInfo(profile.getZoneInfo());
        idToken.setLocale(profile.getLocale());
        Optional<Long> updatedAt = Optional.of(profile.getUpdatedAt().toEpochSecond());
        idToken.setUpdatedAt(updatedAt);
    }

    @Override
    public void toEmailClaims(IdToken idToken, String email, Boolean isVerified) {
        idToken.setEmail(Optional.of(email));
        idToken.setEmailVerified(Optional.of(isVerified));
    }

    @Override
    public void toPhoneClaims(IdToken idToken, Optional<String> phone, Boolean isVerified) {
        idToken.setPhoneNumber(phone);
        idToken.setPhoneNumberVerified(Optional.of(isVerified));
    }

    @Override
    public Optional<String> makeFamiyNamesClaim(List<FamilyName> familyNames) {
        String names = null;
        for(FamilyName familyName: familyNames) {
            if (names == null) {
                names = familyName.getName();
            } else {
                names += " " + familyName.getName();
            }
        }

        Optional<String> namesClaim = Optional.empty();
        if (names != null) {
            namesClaim = Optional.of(names);
        }
        return namesClaim;
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
