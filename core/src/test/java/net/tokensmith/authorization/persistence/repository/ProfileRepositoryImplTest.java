package net.tokensmith.authorization.persistence.repository;

import helper.fixture.FixtureFactory;
import net.tokensmith.repository.repo.ProfileRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.repository.entity.Profile;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.mapper.ProfileMapper;

import java.net.URISyntaxException;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 3/17/16.
 */
public class ProfileRepositoryImplTest {

    @Mock
    private ProfileMapper mockProfileMapper;

    private ProfileRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new ProfileRepositoryImpl(mockProfileMapper);
    }

    @Test
    public void insert() throws URISyntaxException {
        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(resourceOwner.getId());

        subject.insert(profile);
        verify(mockProfileMapper, times(1)).insert(profile);
    }

    @Test
    public void testGetByResourceOwnerIdShouldReturnProfile() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();

        Profile profile = FixtureFactory.makeProfile(ro.getId());
        when(mockProfileMapper.getByResourceId(ro.getId())).thenReturn(profile);

        Profile actual = subject.getByResourceOwnerId(ro.getId());
        assertThat(actual, is(profile));
    }

    @Test(expected = RecordNotFoundException.class)
    public void testGetByResourceOwnerIdShouldThrowRecordNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(mockProfileMapper.getByResourceId(id)).thenReturn(null);

        subject.getByResourceOwnerId(id);
    }

}