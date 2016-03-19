package org.rootservices.authorization.grant.openid.protocol.token.translator;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.grant.openid.protocol.token.response.entity.IdToken;
import org.rootservices.authorization.persistence.entity.FamilyName;
import org.rootservices.authorization.persistence.entity.Gender;
import org.rootservices.authorization.persistence.entity.GivenName;
import org.rootservices.authorization.persistence.entity.Profile;

import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 3/17/16.
 */
public class ProfileToIdTokenImplTest {

    private ProfileToIdToken subject;

    @Before
    public void setUp() {
        subject = new ProfileToIdTokenImpl();
    }

    @Test
    public void toProfileClaimsShouldAssign() throws Exception {
        Profile profile = FixtureFactory.makeProfile(UUID.randomUUID());
        profile.setUpdatedAt(OffsetDateTime.now());

        FamilyName familyName = FixtureFactory.makeFamilyName(profile.getId());
        profile.getFamilyNames().add(familyName);

        GivenName givenName = FixtureFactory.makeGivenName(profile.getId());
        profile.getGivenNames().add(givenName);

        IdToken actual = new IdToken();
        subject.toProfileClaims(actual, profile);

        assertThat(actual.getLastName().isPresent(), is(true));
        assertThat(actual.getLastName().get(), is(familyName.getName()));

        assertThat(actual.getFirstName().isPresent(), is(true));
        assertThat(actual.getFirstName().get(), is(givenName.getName()));

        assertThat(actual.getMiddleName().isPresent(), is(false));

        assertThat(actual.getNickName().isPresent(), is(true));
        assertThat(actual.getNickName().get(), is(profile.getNickName().get()));

        assertThat(actual.getPreferredUsername().isPresent(), is(true));
        assertThat(actual.getPreferredUsername().get(), is(profile.getPreferredUserName().get()));

        assertThat(actual.getProfile().isPresent(), is(true));
        assertThat(actual.getProfile().get(), is(profile.getProfile().get()));

        assertThat(actual.getPicture().isPresent(), is(true));
        assertThat(actual.getPicture().get(), is(profile.getPicture().get()));

        assertThat(actual.getWebsite().isPresent(), is(true));
        assertThat(actual.getWebsite().get(), is(profile.getWebsite().get()));

        assertThat(actual.getGender().isPresent(), is(true));
        assertThat(actual.getGender().get(), is(profile.getGender().get().toString()));

        assertThat(actual.getBirthdate().isPresent(), is(false));

        assertThat(actual.getZoneInfo().isPresent(), is(false));

        assertThat(actual.getLocale().isPresent(), is(false));

        assertThat(actual.getUpdatedAt().isPresent(), is(true));
        assertThat(actual.getUpdatedAt().get(), is(profile.getUpdatedAt().toEpochSecond()));
    }

    @Test
    public void toEmailClaimsNotVerifiedShouldAssign() throws Exception {
        String email = FixtureFactory.makeRandomEmail();

        IdToken actual = new IdToken();
        subject.toEmailClaims(actual, email, false);

        assertThat(actual.getEmail().isPresent(), is(true));
        assertThat(actual.getEmail().get(), is(email));
        assertThat(actual.getEmailVerified().isPresent(), is(true));
        assertThat(actual.getEmailVerified().get(), is(false));
    }

    @Test
    public void toEmailClaimsIsVerifiedShouldAssign() throws Exception {
        String email = FixtureFactory.makeRandomEmail();

        IdToken actual = new IdToken();
        subject.toEmailClaims(actual, email, true);

        assertThat(actual.getEmail().isPresent(), is(true));
        assertThat(actual.getEmail().get(), is(email));
        assertThat(actual.getEmailVerified().isPresent(), is(true));
        assertThat(actual.getEmailVerified().get(), is(true));
    }

    @Test
    public void toPhoneClaimsNotVerifiedShouldAssign() throws Exception {
        Optional<String> phone = Optional.of("555-555-5555");

        IdToken actual = new IdToken();
        subject.toPhoneClaims(actual, phone, false);

        assertThat(actual.getPhoneNumber().isPresent(), is(true));
        assertThat(actual.getPhoneNumber().get(), is(phone.get()));
        assertThat(actual.getPhoneNumberVerified().isPresent(), is(true));
        assertThat(actual.getPhoneNumberVerified().get(), is(false));
    }

    @Test
    public void toPhoneClaimsIsVerifiedShouldAssign() throws Exception {
        Optional<String> phone = Optional.of("555-555-5555");

        IdToken actual = new IdToken();
        subject.toPhoneClaims(actual, phone, true);

        assertThat(actual.getPhoneNumber().isPresent(), is(true));
        assertThat(actual.getPhoneNumber().get(), is(phone.get()));
        assertThat(actual.getPhoneNumberVerified().isPresent(), is(true));
        assertThat(actual.getPhoneNumberVerified().get(), is(true));
    }

    @Test
    public void toPhoneClaimsWhenPhoneIsNotPresentShouldAssign() throws Exception {
        Optional<String> phone = Optional.empty();

        IdToken actual = new IdToken();
        subject.toPhoneClaims(actual, phone, false);

        assertThat(actual.getPhoneNumber().isPresent(), is(false));
        assertThat(actual.getPhoneNumberVerified().isPresent(), is(true));
        assertThat(actual.getPhoneNumberVerified().get(), is(false));
    }

    @Test
    public void makeGivenNamesClaimWhenManyShouldAssign() throws Exception {
        GivenName firstGivenName = FixtureFactory.makeGivenName(UUID.randomUUID());
        GivenName secondGivenName = FixtureFactory.makeGivenName(UUID.randomUUID());

        List<GivenName> givenNames = new ArrayList<>();
        givenNames.add(firstGivenName);
        givenNames.add(secondGivenName);

        Optional<String> actual = subject.makeGivenNamesClaim(givenNames);
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is("Obi-Wan Obi-Wan"));
    }

    @Test
    public void makeGivenNamesClaimWhenOneShouldAssign() throws Exception {
        GivenName givenName = FixtureFactory.makeGivenName(UUID.randomUUID());

        List<GivenName> givenNames = new ArrayList<>();
        givenNames.add(givenName);

        Optional<String> actual = subject.makeGivenNamesClaim(givenNames);
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is("Obi-Wan"));
    }

    @Test
    public void makeGivenNamesClaimWhenEmptyShouldAssign() throws Exception {
        List<GivenName> givenNames = new ArrayList<>();

        Optional<String> actual = subject.makeGivenNamesClaim(givenNames);
        assertThat(actual.isPresent(), is(false));
    }

    @Test
    public void makeFamilyNamesClaimWhenManyShouldAssign() throws Exception {
        FamilyName firstFamilyName = FixtureFactory.makeFamilyName(UUID.randomUUID());
        FamilyName secondFamilyName = FixtureFactory.makeFamilyName(UUID.randomUUID());

        List<FamilyName> familyNames = new ArrayList<>();
        familyNames.add(firstFamilyName);
        familyNames.add(secondFamilyName);

        Optional<String> actual = subject.makeFamiyNamesClaim(familyNames);
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is("Kenobi Kenobi"));
    }

    @Test
    public void makeFamilyNamesClaimWhenOneShouldAssign() throws Exception {
        FamilyName familyName = FixtureFactory.makeFamilyName(UUID.randomUUID());

        List<FamilyName> familyNames = new ArrayList<>();
        familyNames.add(familyName);

        Optional<String> actual = subject.makeFamiyNamesClaim(familyNames);
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is("Kenobi"));
    }

    @Test
    public void makeFamilyNamesClaimWhenEmptyShouldAssign() throws Exception {
        List<FamilyName> familyNames = new ArrayList<>();

        Optional<String> actual = subject.makeFamiyNamesClaim(familyNames);
        assertThat(actual.isPresent(), is(false));
    }

    @Test
    public void makeGivenNamesClaimWhenEmptyShouldNotBePresent() throws Exception {
        List<GivenName> givenNames = new ArrayList<>();

        Optional<String> actual = subject.makeGivenNamesClaim(givenNames);
        assertThat(actual.isPresent(), is(false));
    }

    @Test
    public void makeGenderClaimShouldBeMale() throws Exception {
        Optional<Gender> gender = Optional.of(Gender.MALE);

        Optional<String> actual = subject.makeGenderClaim(gender);
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is("MALE"));
    }

    @Test
    public void makeGenderClaimWhenNotPresentShouldBeNotPresent() throws Exception {
        Optional<Gender> gender = Optional.empty();

        Optional<String> actual = subject.makeGenderClaim(gender);
        assertThat(actual.isPresent(), is(false));
    }
}