package net.tokensmith.authorization.register.translator;

import net.tokensmith.authorization.persistence.entity.*;
import net.tokensmith.authorization.register.request.UserInfo;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Component
public class UserInfoTranslator {

    public ResourceOwner from(UserInfo userInfo) {
        ResourceOwner ro = new ResourceOwner();

        // required fields
        ro.setId(UUID.randomUUID());
        ro.setEmail(userInfo.getEmail());
        ro.setEmailVerified(false);

        // intentionally do not set password since it needs to be encrypted.

        Profile profile = new Profile();
        profile.setId(UUID.randomUUID());
        profile.setResourceOwnerId(ro.getId());

        // optionals
        profile.setName(userInfo.getName());

        List<GivenName> givenNames = new ArrayList<>();
        if (userInfo.getGivenName().isPresent()) {
            GivenName givenName = new GivenName();
            givenName.setId(UUID.randomUUID());
            givenName.setResourceOwnerProfileId(profile.getId());
            givenName.setName(userInfo.getGivenName().get());
            givenNames.add(givenName);
        }
        profile.setGivenNames(givenNames);

        List<FamilyName> familyNames = new ArrayList<>();
        if (userInfo.getFamilyName().isPresent()) {
            FamilyName familyName = new FamilyName();
            familyName.setId(UUID.randomUUID());
            familyName.setResourceOwnerProfileId(profile.getId());
            familyName.setName(userInfo.getFamilyName().get());
            familyNames.add(familyName);
        }
        profile.setFamilyNames(familyNames);

        profile.setMiddleName(userInfo.getMiddleName());
        profile.setNickName(userInfo.getNickName());
        profile.setPreferredUserName(userInfo.getPreferredUserName());
        profile.setProfile(userInfo.getProfile());
        profile.setPicture(userInfo.getPicture());
        profile.setWebsite(userInfo.getWebsite());

        Optional<Gender> gender = Optional.empty();
        if (userInfo.getGender().isPresent()) {
            String g = userInfo.getGender().get().toUpperCase();
            gender = Optional.of(Gender.valueOf(g));
        }
        profile.setGender(gender);
        profile.setBirthDate(Optional.empty());
        if (userInfo.getBirthDate().isPresent()) {
            profile.setBirthDate(Optional.of(OffsetDateTime.of(userInfo.getBirthDate().get(), LocalTime.MIDNIGHT, ZoneOffset.UTC)));
        }
        profile.setZoneInfo(userInfo.getZoneInfo());
        profile.setLocale(userInfo.getLocale());
        profile.setPhoneNumber(userInfo.getPhoneNumber());
        profile.setPhoneNumberVerified(false);

        ro.setProfile(profile);

        List<Address> addresses = new ArrayList<>();
        if (userInfo.getAddress().isPresent()) {
            Address address = new Address();
            address.setId(UUID.randomUUID());
            address.setProfileId(profile.getId());
            address.setStreetAddress(userInfo.getAddress().get().getStreetAddress1());
            address.setStreetAddress2(userInfo.getAddress().get().getStreetAddress2());
            address.setLocality(userInfo.getAddress().get().getLocality());
            address.setRegion(userInfo.getAddress().get().getRegion());
            address.setPostalCode(userInfo.getAddress().get().getPostalCode());
            address.setCountry(userInfo.getAddress().get().getCountry());
            addresses.add(address);
        }
        profile.setAddresses(addresses);

        return ro;
    }
}
