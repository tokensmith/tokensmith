package net.tokensmith.authorization.http.presenter.translator;

import net.tokensmith.authorization.http.presenter.profile.Address;
import net.tokensmith.authorization.http.presenter.profile.ProfilePresenter;
import net.tokensmith.repository.entity.Profile;
import net.tokensmith.repository.entity.ResourceOwner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
public class ProfilePresenterTranslator {

    public ProfilePresenter to(ResourceOwner from) {
        ProfilePresenter to = new ProfilePresenter();
        to.setEmail(from.getEmail());

        Profile fromProfile = from.getProfile();
        to.setId(from.getProfile().getId());
        to.setResourceOwnerId(from.getId());

        if (fromProfile.getName().isPresent())
            to.setName(from.getProfile().getName().get());

        if(fromProfile.getFamilyNames().size() > 0) {
            to.setFamilyNameId(fromProfile.getFamilyNames().get(0).getId());
            to.setFamilyName(fromProfile.getFamilyNames().get(0).getName());
        }

        if(fromProfile.getGivenNames().size() > 0) {
            to.setGivenNameId(fromProfile.getGivenNames().get(0).getId());
            to.setGivenName(fromProfile.getGivenNames().get(0).getName());
        }

        if(fromProfile.getMiddleName().isPresent())
            to.setMiddleName(fromProfile.getMiddleName().get());

        if(fromProfile.getNickName().isPresent())
            to.setNickName(fromProfile.getNickName().get());

        if(fromProfile.getPreferredUserName().isPresent())
            to.setPreferredUserName(fromProfile.getPreferredUserName().get());

        if(fromProfile.getProfile().isPresent())
            to.setProfile(fromProfile.getProfile().get());

        if(fromProfile.getPicture().isPresent())
            to.setProfile(fromProfile.getPicture().get());

        if(fromProfile.getWebsite().isPresent())
            to.setWebsite(fromProfile.getWebsite().get());

        if(fromProfile.getGender().isPresent())
            to.setGender(fromProfile.getGender().get().name());

        if(fromProfile.getBirthDate().isPresent())
            to.setBirthDate(fromProfile.getBirthDate().get());

        if(fromProfile.getZoneInfo().isPresent())
            to.setZoneInfo(fromProfile.getZoneInfo().get());

        if(fromProfile.getLocale().isPresent())
            to.setLocale(fromProfile.getLocale().get());

        if(fromProfile.getPhoneNumber().isPresent())
            to.setPhoneNumber(fromProfile.getPhoneNumber().get());

        to.setAddresses(new ArrayList<>());
        for(net.tokensmith.repository.entity.Address address: fromProfile.getAddresses()) {
            to.getAddresses().add(to(address));
        }

        if (to.getAddresses().size() == 0) {
            to.setHasAddress(false);
        } else {
            to.setHasAddress(true);
        }

        return to;
    }

    public Address to(net.tokensmith.repository.entity.Address from) {
        Address to = new Address();
        to.setId(from.getId());
        to.setStreetAddress(from.getStreetAddress());

        if (from.getStreetAddress2().isPresent())
            to.setStreetAddress2(from.getStreetAddress2());


        to.setLocality(from.getLocality());
        to.setRegion(from.getRegion());
        to.setPostalCode(from.getPostalCode());
        to.setCountry(from.getCountry());

        return to;
    }


}
