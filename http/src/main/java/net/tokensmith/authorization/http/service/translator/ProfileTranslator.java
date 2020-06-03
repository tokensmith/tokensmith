package net.tokensmith.authorization.http.service.translator;


import net.tokensmith.repository.entity.Name;
import net.tokensmith.repository.entity.Gender;
import net.tokensmith.repository.entity.Profile;
import net.tokensmith.repository.entity.ResourceOwner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class ProfileTranslator {
    protected static Logger LOGGER = LoggerFactory.getLogger(ProfileTranslator.class);

    public Profile toEntity(net.tokensmith.authorization.http.controller.resource.api.site.model.Profile from) {
        Profile to = new Profile();
        to.setId(from.getId());
        to.setResourceOwnerId(from.getResourceOwnerId());
        to.setName(from.getName());
        to.setMiddleName(from.getMiddleName());
        to.setNickName(from.getNickName());
        to.setPreferredUserName(from.getPreferredUserName());
        to.setProfile(from.getProfile());
        to.setPicture(from.getPicture());
        to.setWebsite(from.getWebsite());
        to.setGender(Optional.empty());

        if (from.getGender().isPresent()) {
            try {
                Gender toGender = Gender.valueOf(from.getGender().get().toUpperCase());
                to.setGender(Optional.of(toGender));
            } catch (IllegalArgumentException e) {
                LOGGER.info(e.getMessage(), e);
            }
        }

        to.setBirthDate(from.getBirthDate());
        to.setZoneInfo(from.getZoneInfo());
        to.setLocale(from.getLocale());
        to.setPhoneNumber(from.getPhoneNumber());

        return to;
    }

    public Name toEntity(net.tokensmith.authorization.http.controller.resource.api.site.model.Name from) {
        Name to = new Name();
        to.setId(from.getId());
        to.setResourceOwnerProfileId(from.getProfileId());
        to.setName(from.getName());

        return to;
    }

    public net.tokensmith.authorization.http.controller.resource.api.site.model.Profile toModel(ResourceOwner from) {
        net.tokensmith.authorization.http.controller.resource.api.site.model.Profile to = new net.tokensmith.authorization.http.controller.resource.api.site.model.Profile();

        Profile fromProfile = from.getProfile();

        to.setEmail(from.getEmail());
        to.setId(fromProfile.getId());
        to.setResourceOwnerId(from.getId());
        to.setName(fromProfile.getName());
        to.setMiddleName(fromProfile.getMiddleName());
        to.setNickName(fromProfile.getNickName());
        to.setPreferredUserName(fromProfile.getPreferredUserName());
        to.setProfile(fromProfile.getProfile());
        to.setPicture(fromProfile.getPicture());
        to.setWebsite(fromProfile.getWebsite());
        to.setGender(Optional.empty());

        if (fromProfile.getGender().isPresent()) {
            Optional<String> toGender = Optional.of(fromProfile.getGender().get().toString().toLowerCase());
            to.setGender(toGender);
        }

        to.setBirthDate(fromProfile.getBirthDate());
        to.setZoneInfo(fromProfile.getZoneInfo());
        to.setLocale(fromProfile.getLocale());
        to.setPhoneNumber(fromProfile.getPhoneNumber());

        // given names
        if(fromProfile.getGivenNames().size() > 0)
            to.setGivenName(toModel(fromProfile.getGivenNames().get(0)));

        // given names
        if(fromProfile.getFamilyNames().size() > 0)
            to.setFamilyName(toModel(fromProfile.getFamilyNames().get(0)));

        return to;
    }

    public net.tokensmith.authorization.http.controller.resource.api.site.model.Name toModel(Name from) {
        net.tokensmith.authorization.http.controller.resource.api.site.model.Name to = new net.tokensmith.authorization.http.controller.resource.api.site.model.Name();
        to.setId(from.getId());
        to.setProfileId(from.getResourceOwnerProfileId());
        to.setName(from.getName());

        return to;
    }

}
