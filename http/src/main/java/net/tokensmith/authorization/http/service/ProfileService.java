package net.tokensmith.authorization.http.service;

import net.tokensmith.authorization.http.controller.resource.api.site.model.Address;
import net.tokensmith.authorization.http.controller.resource.api.site.model.Name;
import net.tokensmith.authorization.http.controller.resource.api.site.model.Profile;
import net.tokensmith.authorization.http.service.translator.AddressTranslator;
import net.tokensmith.authorization.http.service.translator.ProfileTranslator;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.AddressRepository;
import net.tokensmith.repository.repo.FamilyNameRepository;
import net.tokensmith.repository.repo.GivenNameRepository;
import net.tokensmith.repository.repo.ProfileRepository;
import net.tokensmith.repository.repo.ResourceOwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class ProfileService {
    protected static Logger LOGGER = LoggerFactory.getLogger(ProfileService.class);

    private AddressTranslator addressTranslator;
    private AddressRepository addressRepo;
    private ResourceOwnerRepository resourceOwnerRepository;
    private ProfileTranslator profileTranslator;
    private ProfileRepository profileRepository;
    private GivenNameRepository givenNameRepository;
    private FamilyNameRepository familyNameRepository;

    @Autowired
    public ProfileService(AddressTranslator addressTranslator, AddressRepository addressRepo, ResourceOwnerRepository resourceOwnerRepository, ProfileTranslator profileTranslator, ProfileRepository profileRepository, GivenNameRepository givenNameRepository, FamilyNameRepository familyNameRepository) {
        this.addressTranslator = addressTranslator;
        this.addressRepo = addressRepo;
        this.resourceOwnerRepository = resourceOwnerRepository;
        this.profileTranslator = profileTranslator;
        this.profileRepository = profileRepository;
        this.givenNameRepository = givenNameRepository;
        this.familyNameRepository = familyNameRepository;
    }

    public Address createAddress(Address address) {
        // does this need any protection, verify the user owns the profile?
        var to = addressTranslator.toEntity(address);

        if (Objects.isNull(to.getId())) {
            to.setId(UUID.randomUUID());
        }
        addressRepo.insert(to);

        var addressAtRest = addressTranslator.toModel(to);
        return addressAtRest;
    }

    public void deleteAddress(UUID id, UUID resourceOwnerId) {
        addressRepo.delete(id, resourceOwnerId);
    }

    public Address updateAddress(UUID resourceOwnerId, Address address) {
        var to = addressTranslator.toEntity(address);
        addressRepo.update(resourceOwnerId, to);
        var addressAtRest = addressTranslator.toModel(to);
        return addressAtRest;
    }

    public Profile updateProfile(UUID resourceOwnerId, Profile profile) {

        ResourceOwner resourceOwnerAtRest = resourceOwner(resourceOwnerId);

        var to = profileTranslator.toEntity(profile);

        if (resourceOwnerAtRest.getProfile().getPhoneNumber().equals(to.getPhoneNumber())) {
            to.setPhoneNumberVerified(resourceOwnerAtRest.getProfile().isPhoneNumberVerified());
        } else {
            to.setPhoneNumberVerified(false);
            // TODO: publish message / text or email user.
        }

        if (!resourceOwnerAtRest.getEmail().equals(profile.getEmail())) {
            resourceOwnerRepository.updateEmail(resourceOwnerId, profile.getEmail());
            // TODO: publish message / text or email user.
        }

        profileRepository.update(resourceOwnerId, to);

        // crud given name
        givenName(
            resourceOwnerId,
            resourceOwnerAtRest.getProfile().getId(),
            profile.getGivenName(),
            resourceOwnerAtRest.getProfile().getGivenNames()
        );

        // crud family name
        familyName(
            resourceOwnerId,
            resourceOwnerAtRest.getProfile().getId(),
            profile.getFamilyName(),
            resourceOwnerAtRest.getProfile().getFamilyNames()
        );

        resourceOwnerAtRest = resourceOwner(resourceOwnerId);
        return profileTranslator.toModel(resourceOwnerAtRest);
    }

    protected ResourceOwner resourceOwner(UUID resourceOwnerId) {
        ResourceOwner resourceOwnerAtRest = null;
        try {
            resourceOwnerAtRest = resourceOwnerRepository.getByIdWithProfile(resourceOwnerId);
        } catch (RecordNotFoundException e) {
            // unlikely as it should exist if users get this far... they are authenticated
            LOGGER.error(e.getMessage(), e);
        }
        return resourceOwnerAtRest;
    }

    public void givenName(UUID resourceOwnerId, UUID profileId, Name givenNameApi, List<net.tokensmith.repository.entity.Name> givenNamesAtRest) {
        net.tokensmith.repository.entity.Name to = profileTranslator.toEntity(givenNameApi);

        if (canInsertName(profileId, givenNameApi, givenNamesAtRest)) {
            to.setId(UUID.randomUUID());
            givenNameRepository.insert(to);
        } else if (canUpdateName(profileId, givenNameApi, givenNamesAtRest)) {
            to.setUpdatedAt(OffsetDateTime.now());
            givenNameRepository.update(resourceOwnerId, to);
        } else if(canDeleteName(profileId, givenNameApi, givenNamesAtRest)) {
            givenNameRepository.delete(resourceOwnerId, to);
        }
    }

    public void familyName(UUID resourceOwnerId, UUID profileId, Name familyNameApi, List<net.tokensmith.repository.entity.Name> familiyNamesAtRest) {

        net.tokensmith.repository.entity.Name to = profileTranslator.toEntity(familyNameApi);
        if (canInsertName(profileId, familyNameApi, familiyNamesAtRest)) {
            to.setId(UUID.randomUUID());
            familyNameRepository.insert(to);
        } else if (canUpdateName(profileId, familyNameApi, familiyNamesAtRest)) {
            to.setUpdatedAt(OffsetDateTime.now());
            familyNameRepository.update(resourceOwnerId, to);
        } else if (canDeleteName(profileId, familyNameApi, familiyNamesAtRest)) {
            familyNameRepository.update(resourceOwnerId, to);
        }
    }

    protected boolean canInsertName(UUID profileIdAtRest, Name nameModel, List<net.tokensmith.repository.entity.Name> namesAtRest) {
        return Objects.nonNull(nameModel) &&
                Objects.isNull(nameModel.getId()) &&
                !nameModel.getName().isBlank() &&
                namesAtRest.size() == 0 &&
                Objects.nonNull(nameModel.getProfileId()) &&
                nameModel.getProfileId().equals(profileIdAtRest);
    }

    protected boolean canUpdateName(UUID profileIdAtRest, Name nameModel, List<net.tokensmith.repository.entity.Name> namesAtRest) {
        return Objects.nonNull(nameModel) &&
                Objects.nonNull(nameModel.getId()) &&
                !nameModel.getName().isBlank() &&
                namesAtRest.size() == 1 &&
                Objects.nonNull(nameModel.getProfileId()) &&
                nameModel.getProfileId().equals(profileIdAtRest);
    }

    protected boolean canDeleteName(UUID profileIdAtRest, Name nameModel, List<net.tokensmith.repository.entity.Name> namesAtRest) {
        return Objects.nonNull(nameModel) &&
                Objects.nonNull(nameModel.getId()) &&
                (Objects.isNull(nameModel.getName()) || nameModel.getName().isBlank()) &&
                namesAtRest.size() == 1 &&
                Objects.nonNull(nameModel.getProfileId()) &&
                nameModel.getProfileId().equals(profileIdAtRest);
    }
}
