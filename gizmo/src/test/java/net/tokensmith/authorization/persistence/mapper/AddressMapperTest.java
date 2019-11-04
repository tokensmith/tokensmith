package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.TestAppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.tokensmith.repository.entity.Address;
import net.tokensmith.repository.entity.Profile;
import net.tokensmith.repository.entity.ResourceOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 3/12/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= TestAppConfig.class, loader= AnnotationConfigContextLoader.class)
@Transactional
public class AddressMapperTest {

    @Autowired
    private ResourceOwnerMapper resourceOwnerMapper;

    @Autowired
    private ProfileMapper profileMapper;

    @Autowired
    private AddressMapper subject;

    private Profile prepareDbForTest() throws URISyntaxException {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerMapper.insert(ro);

        Profile profile = FixtureFactory.makeProfile(ro.getId());
        profileMapper.insert(profile);

        return profile;
    }

    @Test
    public void insertShouldInsertProfile() throws URISyntaxException {
        Profile profile = prepareDbForTest();
        Address address = FixtureFactory.makeAddress(profile.getId());

        subject.insert(address);
    }

    @Test
    public void getByIdShouldGetAddress() throws URISyntaxException {
        Profile profile = prepareDbForTest();
        Address address = FixtureFactory.makeAddress(profile.getId());
        subject.insert(address);

        Address actual = subject.getById(address.getId());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(notNullValue()));

        assertThat(actual.getStreetAddress(), is("123 Jedi High Council Rd."));
        assertThat(actual.getStreetAddress2().isPresent(), is(false));
        assertThat(actual.getLocality(), is("Coruscant"));
        assertThat(actual.getRegion(), is("Coruscant"));
        assertThat(actual.getPostalCode(), is("12345"));
        assertThat(actual.getCountry(), is("Old Republic"));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
    }
}