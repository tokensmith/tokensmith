package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.TestAppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.tokensmith.repository.entity.GivenName;
import net.tokensmith.repository.entity.Profile;
import net.tokensmith.repository.entity.ResourceOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

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


    public Profile prepareForTest() throws URISyntaxException {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerMapper.insert(ro);

        Profile profile = FixtureFactory.makeProfile(ro.getId());
        profileMapper.insert(profile);

        return profile;
    }

    @Test
    public void insertShouldInsertGivenName() throws URISyntaxException {
        Profile profile = prepareForTest();

        GivenName givenName = FixtureFactory.makeGivenName(profile.getId());
        subject.insert(givenName);
    }

    @Test
    public void findByIdShouldFind() throws URISyntaxException {
        Profile profile = prepareForTest();

        GivenName givenName = FixtureFactory.makeGivenName(profile.getId());
        subject.insert(givenName);

        GivenName actual = subject.findById(givenName.getId());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getResourceOwnerProfileId(), is(givenName.getResourceOwnerProfileId()));
        assertThat(actual.getName(), is(givenName.getName()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
    }

}