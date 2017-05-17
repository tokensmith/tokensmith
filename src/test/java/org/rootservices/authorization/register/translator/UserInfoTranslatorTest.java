package org.rootservices.authorization.register.translator;

import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.persistence.entity.Gender;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.register.request.Address;
import org.rootservices.authorization.register.request.UserInfo;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;


public class UserInfoTranslatorTest {

    private UserInfoTranslator subject;

    @Before
    public void setUp() {
        subject = new UserInfoTranslator();
    }

    @Test
    public void fromWhenRequiredFieldsOnly() {
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail("obi-wan@rootservices.org");
        userInfo.setPassword("password");

        userInfo.setName(Optional.empty());
        userInfo.setFamilyName(Optional.empty());
        userInfo.setGivenName(Optional.empty());
        userInfo.setMiddleName(Optional.empty());
        userInfo.setNickName(Optional.empty());
        userInfo.setPreferredUserName(Optional.empty());
        userInfo.setProfile(Optional.empty());
        userInfo.setPicture(Optional.empty());
        userInfo.setWebsite(Optional.empty());
        userInfo.setGender(Optional.empty());
        userInfo.setBirthDate(Optional.empty());
        userInfo.setZoneInfo(Optional.empty());
        userInfo.setLocale(Optional.empty());
        userInfo.setPhoneNumber(Optional.empty());
        userInfo.setAddress(Optional.empty());

        ResourceOwner actual = subject.from(userInfo);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual.getEmail(), is("obi-wan@rootservices.org"));
        assertThat(actual.isEmailVerified(), is(false));
        assertThat(actual.getPassword(), is(nullValue()));

        // profile
        assertThat(actual.getProfile(), is(notNullValue()));
        assertThat(actual.getProfile().getId(), is(notNullValue()));
        assertThat(actual.getProfile().getName().isPresent(), is(false));

        assertThat(actual.getProfile().getGivenNames(), is(notNullValue()));
        assertThat(actual.getProfile().getGivenNames().size(), is(0));

        assertThat(actual.getProfile().getFamilyNames(), is(notNullValue()));
        assertThat(actual.getProfile().getFamilyNames().size(), is(0));

        assertThat(actual.getProfile().getMiddleName().isPresent(), is(false));
        assertThat(actual.getProfile().getNickName().isPresent(), is(false));
        assertThat(actual.getProfile().getPreferredUserName().isPresent(), is(false));
        assertThat(actual.getProfile().getProfile().isPresent(), is(false));
        assertThat(actual.getProfile().getPicture().isPresent(), is(false));
        assertThat(actual.getProfile().getWebsite().isPresent(), is(false));
        assertThat(actual.getProfile().getGender().isPresent(), is(false));
        assertThat(actual.getProfile().getBirthDate().isPresent(), is(false));
        assertThat(actual.getProfile().getZoneInfo().isPresent(), is(false));

        assertThat(actual.getProfile().getLocale().isPresent(), is(false));
        assertThat(actual.getProfile().getPhoneNumber().isPresent(), is(false));
        assertThat(actual.getProfile().isPhoneNumberVerified(), is(false));

        assertThat(actual.getProfile().getAddresses(), is(notNullValue()));
        assertThat(actual.getProfile().getAddresses().size(), is(0));
    }

    @Test
    public void fromWhenRequiredAndOptionalFields() throws Exception {

        UserInfo userInfo = new UserInfo();
        userInfo.setEmail("obi-wan@rootservices.org");
        userInfo.setPassword("password");

        userInfo.setName(Optional.of("Obi-Wan Kenobi"));
        userInfo.setFamilyName(Optional.of("Kenobi"));
        userInfo.setGivenName(Optional.of("Obi-Wan"));
        userInfo.setMiddleName(Optional.of("Wan"));
        userInfo.setNickName(Optional.of("Ben"));
        userInfo.setPreferredUserName(Optional.of("Ben Kenobi"));
        userInfo.setProfile(Optional.of(new URI("http://starwars.wikia.com/wiki/Obi-Wan_Kenobi")));
        userInfo.setPicture(Optional.of(new URI("http://vignette1.wikia.nocookie.net/starwars/images/2/25/Kenobi_Maul_clash.png/revision/latest?cb=20130120033039")));
        userInfo.setWebsite(Optional.of(new URI("http://starwars.wikia.com")));
        userInfo.setGender(Optional.of("male"));
        userInfo.setBirthDate(Optional.of(OffsetDateTime.of(3220, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)));
        userInfo.setZoneInfo(Optional.of("America/Chicago"));
        userInfo.setLocale(Optional.of("en-US"));
        userInfo.setPhoneNumber(Optional.of("123-456-7891"));

        Address address = new Address();
        address.setStreetAddress1("123 Best Jedi Lane");
        address.setStreetAddress2(Optional.of("#1"));
        address.setLocality("Chicago");
        address.setRegion("IL");
        address.setPostalCode("60606");
        address.setCountry("Coruscant");

        userInfo.setAddress(Optional.of(address));

        ResourceOwner actual = subject.from(userInfo);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual.getEmail(), is("obi-wan@rootservices.org"));
        assertThat(actual.isEmailVerified(), is(false));
        assertThat(actual.getPassword(), is(nullValue()));

        // profile
        assertThat(actual.getProfile(), is(notNullValue()));
        assertThat(actual.getProfile().getId(), is(notNullValue()));
        assertThat(actual.getProfile().getName().isPresent(), is(true));
        assertThat(actual.getProfile().getName().get(), is("Obi-Wan Kenobi"));
        ;
        assertThat(actual.getProfile().getGivenNames(), is(notNullValue()));
        assertThat(actual.getProfile().getGivenNames().size(), is(1));
        assertThat(actual.getProfile().getGivenNames().get(0).getName(), is("Obi-Wan"));

        assertThat(actual.getProfile().getFamilyNames(), is(notNullValue()));
        assertThat(actual.getProfile().getFamilyNames().size(), is(1));
        assertThat(actual.getProfile().getFamilyNames().get(0).getName(), is("Kenobi"));

        assertThat(actual.getProfile().getMiddleName().isPresent(), is(true));
        assertThat(actual.getProfile().getMiddleName().get(), is("Wan"));

        assertThat(actual.getProfile().getNickName().isPresent(), is(true));
        assertThat(actual.getProfile().getNickName().get(), is("Ben"));

        assertThat(actual.getProfile().getPreferredUserName().isPresent(), is(true));
        assertThat(actual.getProfile().getPreferredUserName().get(), is("Ben Kenobi"));

        assertThat(actual.getProfile().getProfile().isPresent(), is(true));
        assertThat(actual.getProfile().getProfile().get(), is(new URI("http://starwars.wikia.com/wiki/Obi-Wan_Kenobi")));

        assertThat(actual.getProfile().getPicture().isPresent(), is(true));
        assertThat(actual.getProfile().getPicture().get(), is(new URI("http://vignette1.wikia.nocookie.net/starwars/images/2/25/Kenobi_Maul_clash.png/revision/latest?cb=20130120033039")));

        assertThat(actual.getProfile().getWebsite().isPresent(), is(true));
        assertThat(actual.getProfile().getWebsite().get(), is(new URI("http://starwars.wikia.com")));

        assertThat(actual.getProfile().getGender().isPresent(), is(true));
        assertThat(actual.getProfile().getGender().get(), is(Gender.MALE));

        assertThat(actual.getProfile().getBirthDate().isPresent(), is(true));
        assertThat(actual.getProfile().getBirthDate().get(), is(OffsetDateTime.of(3220, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)));

        assertThat(actual.getProfile().getZoneInfo().isPresent(), is(true));
        assertThat(actual.getProfile().getZoneInfo().get(), is("America/Chicago"));

        assertThat(actual.getProfile().getLocale().isPresent(), is(true));
        assertThat(actual.getProfile().getLocale().get(), is("en-US"));

        assertThat(actual.getProfile().getPhoneNumber().isPresent(), is(true));
        assertThat(actual.getProfile().getPhoneNumber().get(), is("123-456-7891"));

        assertThat(actual.getProfile().isPhoneNumberVerified(), is(false));

        assertThat(actual.getProfile().getAddresses(), is(notNullValue()));
        assertThat(actual.getProfile().getAddresses().size(), is(1));

        assertThat(actual.getProfile().getAddresses().get(0).getStreetAddress(), is("123 Best Jedi Lane"));
        assertThat(actual.getProfile().getAddresses().get(0).getStreetAddress2().isPresent(), is(true));
        assertThat(actual.getProfile().getAddresses().get(0).getStreetAddress2().get(), is("#1"));
        assertThat(actual.getProfile().getAddresses().get(0).getLocality(), is("Chicago"));
        assertThat(actual.getProfile().getAddresses().get(0).getRegion(), is("IL"));
        assertThat(actual.getProfile().getAddresses().get(0).getPostalCode(), is("60606"));
        assertThat(actual.getProfile().getAddresses().get(0).getCountry(), is("Coruscant"));
    }

}