package helpers.fixture.persistence.db;


import net.tokensmith.repository.entity.Gender;
import net.tokensmith.repository.entity.Profile;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.repo.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 3/27/16.
 */
@Component
public class LoadOpenIdResourceOwner {
    private LoadResourceOwner loadResourceOwner;
    private ProfileRepository profileRepository;

    @Autowired
    public LoadOpenIdResourceOwner(LoadResourceOwner loadResourceOwner, ProfileRepository profileRepository) {
        this.loadResourceOwner = loadResourceOwner;
        this.profileRepository = profileRepository;
    }

    public ResourceOwner run() throws URISyntaxException, DuplicateRecordException {
        ResourceOwner ro = loadResourceOwner.run();
        Profile profile = makeProfile(ro.getId());
        ro.setProfile(profile);
        profileRepository.insert(profile);
        return ro;
    }

    protected Profile makeProfile(UUID resourceOwnerId) throws URISyntaxException {
        Profile profile = new Profile();

        profile.setId(UUID.randomUUID());
        profile.setResourceOwnerId(resourceOwnerId);
        profile.setName(Optional.of("Obi-Wan Kenobi"));
        profile.setMiddleName(Optional.empty());
        profile.setNickName(Optional.of("Ben"));
        profile.setPreferredUserName(Optional.of("Ben Kenobi"));
        profile.setProfile(Optional.of(new URI("http://starwars.wikia.com/wiki/Obi-Wan_Kenobi")));
        profile.setPicture(Optional.of(new URI("http://vignette1.wikia.nocookie.net/starwars/images/2/25/Kenobi_Maul_clash.png/revision/latest?cb=20130120033039")));
        profile.setWebsite(Optional.of(new URI("http://starwars.wikia.com")));
        profile.setGender(Optional.of(Gender.MALE));
        profile.setBirthDate(Optional.empty());
        profile.setZoneInfo(Optional.empty());
        profile.setLocale(Optional.empty());
        profile.setPhoneNumber(Optional.empty());
        profile.setPhoneNumberVerified(false);

        return profile;
    }
}
