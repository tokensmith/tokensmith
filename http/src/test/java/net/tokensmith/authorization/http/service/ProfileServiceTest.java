package net.tokensmith.authorization.http.service;

import helpers.fixture.EntityFactory;
import helpers.fixture.ModelFactory;
import net.tokensmith.authorization.http.service.translator.AddressTranslator;
import net.tokensmith.authorization.http.service.translator.ProfileTranslator;
import net.tokensmith.repository.entity.Address;
import net.tokensmith.repository.entity.Name;
import net.tokensmith.repository.entity.Profile;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.repo.AddressRepository;
import net.tokensmith.repository.repo.FamilyNameRepository;
import net.tokensmith.repository.repo.GivenNameRepository;
import net.tokensmith.repository.repo.ProfileRepository;
import net.tokensmith.repository.repo.ResourceOwnerRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProfileServiceTest {

    @Mock
    private AddressTranslator mockAddressTranslator;
    @Mock
    private AddressRepository mockAddressRepository;
    @Mock
    private ResourceOwnerRepository mockResourceOwnerRepository;
    @Mock
    private ProfileTranslator mockProfileTranslator;
    @Mock
    private ProfileRepository mockProfileRepository;
    @Mock
    private GivenNameRepository mockGivenNameRepository;
    @Mock
    private FamilyNameRepository mockFamilyNameRepository;

    private ProfileService subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new ProfileService(
                mockAddressTranslator,
                mockAddressRepository,
                mockResourceOwnerRepository,
                mockProfileTranslator,
                mockProfileRepository,
                mockGivenNameRepository,
                mockFamilyNameRepository
        );
    }

    @Test
    public void createAddress() {
        var address = ModelFactory.makeAddress();
        Address addressToInsert = new Address();
        when(mockAddressTranslator.toEntity(address)).thenReturn(addressToInsert);
        when(mockAddressTranslator.toModel(addressToInsert)).thenReturn(address);

        var actual = subject.createAddress(address);

        assertThat(actual, is(address));
        verify(mockAddressRepository).insert(addressToInsert);
    }

    @Test
    public void deleteAddress() {
        UUID addressId = UUID.randomUUID();
        UUID resourceOwnerId = UUID.randomUUID();

        subject.deleteAddress(addressId, resourceOwnerId);

        verify(mockAddressRepository).delete(addressId, resourceOwnerId);
    }

    @Test
    public void updateAddress() {
        UUID resourceOwnerId = UUID.randomUUID();
        var address = ModelFactory.makeAddress();

        Address addressToInsert = new Address();
        when(mockAddressTranslator.toEntity(address)).thenReturn(addressToInsert);
        when(mockAddressTranslator.toModel(addressToInsert)).thenReturn(address);

        var actual = subject.updateAddress(resourceOwnerId, address);

        assertThat(actual, is(address));
        verify(mockAddressRepository).update(eq(resourceOwnerId), eq(addressToInsert));
    }

    @Test
    public void updateProfileShouldBeOk() throws Exception {
        ResourceOwner ro = EntityFactory.makeResourceOwnerWithProfile();
        when(mockResourceOwnerRepository.getByIdWithProfile(ro.getId())).thenReturn(ro);

        var profile = ModelFactory.makeProfile(ro.getId());
        profile.setEmail(ro.getEmail());

        Profile profileToInsert = EntityFactory.makeProfile(ro.getId());
        when(mockProfileTranslator.toEntity(profile)).thenReturn(profileToInsert);

        var profileUpdated = ModelFactory.makeProfile(ro.getId());
        when(mockProfileTranslator.toModel(ro)).thenReturn(profileUpdated);

        var actual = subject.updateProfile(ro.getId(), profile);

        verify(mockResourceOwnerRepository, never()).updateEmail(any(), any());

        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(mockProfileRepository).update(eq(profileToInsert.getResourceOwnerId()), profileCaptor.capture());

        assertThat(actual, is(profileUpdated));

        // ensure phone number was not updated.
        assertThat(profileCaptor.getValue().getPhoneNumber().isPresent(), is(false));
        assertThat(profileCaptor.getValue().isPhoneNumberVerified(), is(false));
    }

    @Test
    public void updateProfileWhenPhoneShouldUpdatePhone() throws Exception {
        ResourceOwner ro = EntityFactory.makeResourceOwnerWithProfile();
        when(mockResourceOwnerRepository.getByIdWithProfile(ro.getId())).thenReturn(ro);

        var profile = ModelFactory.makeProfile(ro.getId());
        profile.setEmail(ro.getEmail());

        Profile profileToInsert = EntityFactory.makeProfile(ro.getId());
        profileToInsert.setPhoneNumber(Optional.of("1234567890"));
        when(mockProfileTranslator.toEntity(profile)).thenReturn(profileToInsert);

        var profileUpdated = ModelFactory.makeProfile(ro.getId());
        when(mockProfileTranslator.toModel(ro)).thenReturn(profileUpdated);

        var actual = subject.updateProfile(ro.getId(), profile);

        verify(mockResourceOwnerRepository, never()).updateEmail(any(), any());

        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(mockProfileRepository).update(eq(profileToInsert.getResourceOwnerId()), profileCaptor.capture());

        assertThat(actual, is(profileUpdated));

        assertThat(profileCaptor.getValue().getPhoneNumber().isPresent(), is(true));
        assertThat(profileCaptor.getValue().getPhoneNumber().get(), is("1234567890"));
        assertThat(profileCaptor.getValue().isPhoneNumberVerified(), is(false));
    }

    @Test
    public void updateProfileWhenEmailShouldUpdateEmail() throws Exception {
        ResourceOwner ro = EntityFactory.makeResourceOwnerWithProfile();
        when(mockResourceOwnerRepository.getByIdWithProfile(ro.getId())).thenReturn(ro);

        var profile = ModelFactory.makeProfile(ro.getId());

        Profile profileToInsert = EntityFactory.makeProfile(ro.getId());
        when(mockProfileTranslator.toEntity(profile)).thenReturn(profileToInsert);

        var profileUpdated = ModelFactory.makeProfile(ro.getId());
        when(mockProfileTranslator.toModel(ro)).thenReturn(profileUpdated);

        var actual = subject.updateProfile(ro.getId(), profile);

        verify(mockResourceOwnerRepository).updateEmail(eq(ro.getId()), eq(profile.getEmail()));

        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(mockProfileRepository).update(eq(profileToInsert.getResourceOwnerId()), profileCaptor.capture());

        assertThat(actual, is(profileUpdated));

        // ensure phone number was not updated.
        assertThat(profileCaptor.getValue().getPhoneNumber().isPresent(), is(false));
        assertThat(profileCaptor.getValue().isPhoneNumberVerified(), is(false));
    }

    @Test
    public void canInsertNameShouldBeTrue() throws Exception {
        ResourceOwner ro = EntityFactory.makeResourceOwnerWithProfile();
        List<Name> givenNamesAtRest = new ArrayList<>();

        var givenNameApi = ModelFactory.makeGivenName(ro.getProfile().getId());
        givenNameApi.setId(null);

        boolean actual = subject.canInsertName(
                ro.getProfile().getId(),
                givenNameApi,
                givenNamesAtRest
        );

        assertTrue(actual);
    }

    @Test
    public void canUpdateNameShouldBeTrue() throws Exception {
        ResourceOwner ro = EntityFactory.makeResourceOwnerWithProfile();
        List<Name> givenNamesAtRest = EntityFactory.givenNames(ro.getProfile().getId());

        var givenNameApi = ModelFactory.makeGivenName(ro.getProfile().getId());

        boolean actual = subject.canUpdateName(
                ro.getProfile().getId(),
                givenNameApi,
                givenNamesAtRest
        );

        assertTrue(actual);
    }

    @Test
    public void canDeleteNameShouldBeTrue() throws Exception {
        ResourceOwner ro = EntityFactory.makeResourceOwnerWithProfile();
        List<Name> givenNamesAtRest = EntityFactory.givenNames(ro.getProfile().getId());

        var givenNameApi = ModelFactory.makeGivenName(ro.getProfile().getId());
        givenNameApi.setName(null);

        boolean actual = subject.canDeleteName(
                ro.getProfile().getId(),
                givenNameApi,
                givenNamesAtRest
        );

        assertTrue(actual);

    }
}