package helpers.fixture;

import net.tokensmith.authorization.http.controller.resource.api.site.model.Address;
import net.tokensmith.authorization.http.controller.resource.api.site.model.Name;
import net.tokensmith.authorization.http.controller.resource.api.site.model.Profile;


import java.net.URI;
import java.util.Optional;
import java.util.UUID;

public class ModelFactory {

    public static Address makeAddress() {
        return makeAddress(UUID.randomUUID());
    }

    public static Address makeAddress(UUID profileId) {
        Address address = new Address();
        address.setId(UUID.randomUUID());
        address.setProfileId(profileId);
        address.setStreetAddress("123 Jedi High Council Rd.");
        address.setStreetAddress2(Optional.empty());
        address.setLocality("Coruscant");
        address.setPostalCode("12345");
        address.setRegion("Coruscant");
        address.setCountry("Old Republic");
        return address;
    }

    public static Profile makeProfile(UUID resourceOwnerId) throws Exception {
        return makeProfile(resourceOwnerId, UUID.randomUUID());
    }

    public static Profile makeProfile(UUID resourceOwnerId, UUID profileId) throws Exception {
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setResourceOwnerId(resourceOwnerId);
        profile.setName(Optional.of("Obi-Wan Kenobi"));
        profile.setMiddleName(Optional.empty());
        profile.setNickName(Optional.of("Ben"));
        profile.setPreferredUserName(Optional.of("Ben Kenobi"));
        profile.setProfile(Optional.of(new URI("http://starwars.wikia.com/wiki/Obi-Wan_Kenobi")));
        profile.setPicture(Optional.of(new URI("http://vignette1.wikia.nocookie.net/starwars/images/2/25/Kenobi_Maul_clash.png/revision/latest?cb=20130120033039")));
        profile.setWebsite(Optional.of(new URI("http://starwars.wikia.com")));
        profile.setGender(Optional.of("male"));
        profile.setBirthDate(Optional.empty());
        profile.setZoneInfo(Optional.empty());
        profile.setLocale(Optional.empty());
        profile.setPhoneNumber(Optional.empty());
        return profile;
    }

    public static Name makeGivenName(UUID profileId) {
        Name givenName = new Name();
        givenName.setId(UUID.randomUUID());
        givenName.setProfileId(profileId);
        givenName.setName("Obi-Wan");
        return givenName;
    }

    public static Name makeFamilyName(UUID profileId) {
        Name familyName = new Name();
        familyName.setId(UUID.randomUUID());
        familyName.setProfileId(profileId);
        familyName.setName("Kenobi");
        return familyName;
    }
}
