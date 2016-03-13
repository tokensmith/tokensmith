package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.Gender;
import org.rootservices.authorization.persistence.entity.Profile;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 2/27/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class ProfileMapperTest {

    @Autowired
    private ResourceOwnerMapper resourceOwnerMapper;

    @Autowired
    private ProfileMapper subject;

    @Test
    public void insertShouldInsertProfile() throws URISyntaxException {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerMapper.insert(ro);

        Profile profile = FixtureFactory.makeProfile(ro.getUuid());
        subject.insert(profile);
    }

    @Test
    public void getByIdShouldGetProfile() throws URISyntaxException {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerMapper.insert(ro);

        Profile profile = FixtureFactory.makeProfile(ro.getUuid());
        subject.insert(profile);

        Profile actual = subject.getById(profile.getId());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual.getResourceOwnerId(), is(notNullValue()));
        assertThat(actual.getName().isPresent(), is(true));
        assertThat(actual.getName().get(), is("Obi-Wan Kenobi"));
        assertThat(actual.getFamilyName().isPresent(), is(true));
        assertThat(actual.getFamilyName().get(), is("Kenobi"));
        assertThat(actual.getMiddleName().isPresent(), is(false));
        assertThat(actual.getNickName().isPresent(), is(true));
        assertThat(actual.getNickName().get(), is("Ben"));
        assertThat(actual.getPreferredUserName().isPresent(), is(true));
        assertThat(actual.getPreferredUserName().get(), is("Ben Kenobi"));
        assertThat(actual.getProfile().isPresent(), is(true));
        assertThat(actual.getProfile().get().toString(), is("http://starwars.wikia.com/wiki/Obi-Wan_Kenobi"));
        assertThat(actual.getPicture().isPresent(), is(true));
        assertThat(actual.getPicture().get().toString(), is("http://vignette1.wikia.nocookie.net/starwars/images/2/25/Kenobi_Maul_clash.png/revision/latest?cb=20130120033039"));
        assertThat(actual.getWebsite().isPresent(), is(true));
        assertThat(actual.getWebsite().get().toString(), is("http://starwars.wikia.com"));
        assertThat(actual.isEmailVerified(), is(false));
        assertThat(actual.getGender().isPresent(), is(true));
        assertThat(actual.getGender().get(), is(Gender.MALE));
        assertThat(actual.getBirthDate().isPresent(), is(false));
        assertThat(actual.getZoneInfo().isPresent(), is(false));
        assertThat(actual.getLocale().isPresent(), is(false));
        assertThat(actual.getPhoneNumber().isPresent(), is(false));
        assertThat(actual.isPhoneNumberVerified(), is(false));
    }
}