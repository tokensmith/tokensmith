package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.TestAppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.tokensmith.repository.entity.Name;
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
 * Created by tommackenzie on 3/19/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= TestAppConfig.class, loader= AnnotationConfigContextLoader.class)
@Transactional
public class FamilyNameMapperTest {
    @Autowired
    private ResourceOwnerMapper resourceOwnerMapper;
    @Autowired
    private ProfileMapper profileMapper;
    @Autowired
    private FamilyNameMapper subject;


    public Profile prepareForTest() throws URISyntaxException {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerMapper.insert(ro);

        Profile profile = FixtureFactory.makeProfile(ro.getId());
        profileMapper.insert(profile);

        return profile;
    }

    @Test
    public void insertShouldInsertFamilyName() throws URISyntaxException {
        Profile profile = prepareForTest();

        Name name = FixtureFactory.makeFamilyName(profile.getId());
        subject.insert(name);
    }

    @Test
    public void findByIdShouldFind() throws URISyntaxException {
        Profile profile = prepareForTest();

        Name name = FixtureFactory.makeFamilyName(profile.getId());
        subject.insert(name);

        Name actual = subject.findById(name.getId());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getResourceOwnerProfileId(), is(name.getResourceOwnerProfileId()));
        assertThat(actual.getName(), is(name.getName()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
    }

    @Test
    public void updateShouldBeOk() throws Exception {
        Profile profile = prepareForTest();

        Name name = FixtureFactory.makeFamilyName(profile.getId());
        subject.insert(name);

        name.setName("foo");

        subject.update(profile.getResourceOwnerId(), name);

        Name actual = subject.findById(name.getId());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getResourceOwnerProfileId(), is(name.getResourceOwnerProfileId()));
        assertThat(actual.getName(), is(name.getName()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
    }

    @Test
    public void updateWhenIncorrectResourceOwnerShouldNotUpdate() throws Exception {
        Profile stealer = prepareForTest();
        Profile legit = prepareForTest();

        Name name = FixtureFactory.makeFamilyName(legit.getId());
        subject.insert(name);

        name.setName("foo");

        subject.update(stealer.getResourceOwnerId(), name);

        Name actual = subject.findById(name.getId());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getResourceOwnerProfileId(), is(name.getResourceOwnerProfileId()));

        // should not have updated.
        assertThat(actual.getName(), is("Kenobi"));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
    }


    @Test
    public void deleteShouldBeOk() throws Exception {
        Profile profile = prepareForTest();

        Name name = FixtureFactory.makeFamilyName(profile.getId());
        subject.insert(name);

        name.setName("foo");

        subject.delete(profile.getResourceOwnerId(), name);

        Name actual = subject.findById(name.getId());

        assertThat(actual, is(nullValue()));
    }

    @Test
    public void deleteWhenIncorrectResourceOwnerShouldNotUpdate() throws Exception {
        Profile stealer = prepareForTest();
        Profile legit = prepareForTest();

        Name name = FixtureFactory.makeFamilyName(legit.getId());
        subject.insert(name);

        name.setName("foo");

        subject.delete(stealer.getResourceOwnerId(), name);

        Name actual = subject.findById(name.getId());

        // should not have deleted.
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getResourceOwnerProfileId(), is(name.getResourceOwnerProfileId()));

        assertThat(actual.getName(), is("Kenobi"));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
    }
}