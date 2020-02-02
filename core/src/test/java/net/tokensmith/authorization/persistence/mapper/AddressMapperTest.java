package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.TestAppConfig;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
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
import java.util.Optional;
import java.util.UUID;

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

    private Profile prepareDbForTest() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerMapper.insert(ro);

        Profile profile = FixtureFactory.makeProfile(ro.getId());
        profileMapper.insert(profile);

        return profile;
    }

    @Test
    public void insertShouldInsertProfile() throws Exception {
        Profile profile = prepareDbForTest();
        Address address = FixtureFactory.makeAddress(profile.getId());

        subject.insert(address);

        assertThat(address.getCreatedAt(), is(notNullValue()));
        assertThat(address.getUpdatedAt(), is(notNullValue()));
    }

    @Test
    public void getByIdShouldGetAddress() throws Exception {
        Profile profile = prepareDbForTest();
        Address address = FixtureFactory.makeAddress(profile.getId());
        subject.insert(address);

        Address actual = subject.getByIdAndResourceOwnerId(address.getId(), profile.getResourceOwnerId());

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

    @Test
    public void updateShouldUpdate() throws Exception {
        Profile profile = prepareDbForTest();
        Address address = FixtureFactory.makeAddress(profile.getId());
        subject.insert(address);

        // modify the address.
        address.setStreetAddress("123 Best Jedi Rd.");
        address.setStreetAddress2(Optional.of("Suite 100"));
        address.setLocality("Tatooine Town");
        address.setPostalCode("67890");
        address.setRegion("Tatooine Town");
        address.setCountry("Tatooine");

        subject.update(profile.getResourceOwnerId(), address);

        // make sure it was updated
        Address actual = subject.getByIdAndResourceOwnerId(address.getId(), profile.getResourceOwnerId());
        assertThat(actual.getStreetAddress(), is("123 Best Jedi Rd."));
        assertThat(actual.getStreetAddress2().isPresent(), is(true));
        assertThat(actual.getStreetAddress2().get(), is("Suite 100"));
        assertThat(actual.getLocality(), is("Tatooine Town"));
        assertThat(actual.getRegion(), is("Tatooine Town"));
        assertThat(actual.getPostalCode(), is("67890"));
        assertThat(actual.getCountry(), is("Tatooine"));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
    }

    @Test
    public void updateWhenIncorrectResourceOwnerShouldNotUpdate() throws Exception {
        Profile profileA = prepareDbForTest();
        Address addressA = FixtureFactory.makeAddress(profileA.getId());
        subject.insert(addressA);

        Profile profileB = prepareDbForTest();
        Address addressB = FixtureFactory.makeAddress(profileB.getId());
        subject.insert(addressB);

        // modify the address.
        addressB.setStreetAddress("123 Best Jedi Rd.");
        addressB.setStreetAddress2(Optional.of("Suite 100"));
        addressB.setLocality("Tatooine Town");
        addressB.setPostalCode("67890");
        addressB.setRegion("Tatooine Town");
        addressB.setCountry("Tatooine");

        subject.update(profileA.getId(), addressB);

        Address actual = subject.getByIdAndResourceOwnerId(addressB.getId(), profileB.getResourceOwnerId());

        // should not update.
        assertThat(actual.getStreetAddress(), is("123 Jedi High Council Rd."));
        assertThat(actual.getStreetAddress2().isPresent(), is(false));
        assertThat(actual.getLocality(), is("Coruscant"));
        assertThat(actual.getRegion(), is("Coruscant"));
        assertThat(actual.getPostalCode(), is("12345"));
        assertThat(actual.getCountry(), is("Old Republic"));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
    }

    @Test
    public void deleteShouldBeOk() throws Exception {
        Profile profileA = prepareDbForTest();
        Address addressA = FixtureFactory.makeAddress(profileA.getId());
        subject.insert(addressA);

        Profile profile = prepareDbForTest();
        Address address = FixtureFactory.makeAddress(profile.getId());
        subject.insert(address);

        subject.delete(address.getId(), profile.getResourceOwnerId());

        Address actual = subject.getByIdAndResourceOwnerId(address.getId(), profile.getResourceOwnerId());

        // should not be there anymore.
        assertThat(actual, is(nullValue()));

        // AddressA should still be there, should not delete other addresses.
        Address actualA = subject.getByIdAndResourceOwnerId(addressA.getId(), profileA.getResourceOwnerId());

        assertThat(actualA, is(notNullValue()));

    }
}