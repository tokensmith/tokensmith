package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.TestAppConfig;
import net.tokensmith.repository.entity.Name;
import net.tokensmith.repository.entity.Profile;
import net.tokensmith.repository.entity.ResourceOwner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by tommackenzie on 3/2/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= TestAppConfig.class, loader= AnnotationConfigContextLoader.class)
@Transactional
public class GivenNameMapperTest {
    @Autowired
    private ResourceOwnerMapper resourceOwnerMapper;
    @Autowired
    private ProfileMapper profileMapper;
    @Autowired
    private GivenNameMapper subject;


    public Profile prepareForTest() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerMapper.insert(ro);

        Profile profile = FixtureFactory.makeProfile(ro.getId());
        profileMapper.insert(profile);

        return profile;
    }

    @Test
    public void insertShouldInsertGivenName() throws Exception {
        Profile profile = prepareForTest();

        Name givenName = FixtureFactory.makeGivenName(profile.getId());
        subject.insert(givenName);
    }

    @Test
    public void findByIdShouldFind() throws Exception {
        Profile profile = prepareForTest();

        Name givenName = FixtureFactory.makeGivenName(profile.getId());
        subject.insert(givenName);

        Name actual = subject.findById(givenName.getId());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getResourceOwnerProfileId(), is(givenName.getResourceOwnerProfileId()));
        assertThat(actual.getName(), is(givenName.getName()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
    }

    @Test
    public void updateShouldBeOk() throws Exception {
        Profile profile = prepareForTest();

        Name givenName = FixtureFactory.makeGivenName(profile.getId());
        subject.insert(givenName);

        givenName.setName("Ben");

        subject.update(profile.getResourceOwnerId(), givenName);

        Name actual = subject.findById(givenName.getId());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getResourceOwnerProfileId(), is(givenName.getResourceOwnerProfileId()));
        assertThat(actual.getName(), is(givenName.getName()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
    }

    @Test
    public void updateWhenIncorrectResourceOwnerShouldNotUpdate() throws Exception {
        Profile stealer = prepareForTest();
        Profile legit = prepareForTest();

        Name givenName = FixtureFactory.makeGivenName(legit.getId());
        subject.insert(givenName);

        givenName.setName("Ben");

        subject.update(stealer.getResourceOwnerId(), givenName);

        Name actual = subject.findById(givenName.getId());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getResourceOwnerProfileId(), is(givenName.getResourceOwnerProfileId()));

        // should not have updated.
        assertThat(actual.getName(), is("Obi-Wan"));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
    }

    @Test
    public void deleteShouldBeOk() throws Exception {
        Profile profile = prepareForTest();

        Name givenName = FixtureFactory.makeGivenName(profile.getId());
        subject.insert(givenName);

        givenName.setName("Ben");

        subject.delete(profile.getResourceOwnerId(), givenName);

        Name actual = subject.findById(givenName.getId());

        assertThat(actual, is(nullValue()));
    }

    @Test
    public void deleteWhenIncorrectResourceOwnerShouldNotUpdate() throws Exception {
        Profile stealer = prepareForTest();
        Profile legit = prepareForTest();

        Name givenName = FixtureFactory.makeGivenName(legit.getId());
        subject.insert(givenName);

        givenName.setName("Ben");

        subject.delete(stealer.getResourceOwnerId(), givenName);

        Name actual = subject.findById(givenName.getId());

        // should not have deleted.
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getResourceOwnerProfileId(), is(givenName.getResourceOwnerProfileId()));

        assertThat(actual.getName(), is("Obi-Wan"));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
    }
}