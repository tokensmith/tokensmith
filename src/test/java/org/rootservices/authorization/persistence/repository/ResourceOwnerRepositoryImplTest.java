package org.rootservices.authorization.persistence.repository;

import helper.fixture.FixtureFactory;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.ResourceOwnerMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 10/11/14.
 */
public class ResourceOwnerRepositoryImplTest {

    @Mock
    private ResourceOwnerMapper mockMapper;

    private ResourceOwnerRepositoryImpl subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new ResourceOwnerRepositoryImpl(mockMapper);
    }


    @Test(expected=RecordNotFoundException.class)
    public void getByUUIDNoRecordFound() throws RecordNotFoundException{
        UUID uuid = UUID.randomUUID();
        when(mockMapper.getById(uuid)).thenReturn(null);
        subject.getById(uuid);
    }

    @Test
    public void getById() throws RecordNotFoundException{

        ResourceOwner expectedAuthUser = FixtureFactory.makeResourceOwner();

        when(mockMapper.getById(expectedAuthUser.getId())).thenReturn(expectedAuthUser);
        ResourceOwner actualAuthUser = subject.getById(expectedAuthUser.getId());
        assertThat(actualAuthUser, is(expectedAuthUser));
    }

    @Test(expected= RecordNotFoundException.class)
    public void getByEmailNoRecordFound() throws RecordNotFoundException{
        String email = "test@rootservices.org";
        byte [] password = "plainTextPassword".getBytes();

        when(mockMapper.getByEmail(email)).thenReturn(null);
        subject.getByEmail(email);
    }

    @Test
    public void getByEmail() throws RecordNotFoundException{

        ResourceOwner expectedAuthUser = FixtureFactory.makeResourceOwner();

        when(mockMapper.getByEmail(
                expectedAuthUser.getEmail())
        ).thenReturn(expectedAuthUser);

        ResourceOwner actualAuthUser = subject.getByEmail(
                expectedAuthUser.getEmail()
        );
        assertThat(actualAuthUser, is(expectedAuthUser));
    }

    @Test(expected= RecordNotFoundException.class)
    public void getByAccessTokenShouldThrowRecordNotFound() throws RecordNotFoundException{
        String accessToken = "access-token";

        when(mockMapper.getByAccessToken(accessToken)).thenReturn(null);
        subject.getByAccessToken(accessToken);
    }

    @Test
    public void getByAccessTokenShouldBeOk() throws RecordNotFoundException{

        String accessToken = "access-token";
        ResourceOwner expectedAuthUser = FixtureFactory.makeResourceOwner();

        when(mockMapper.getByAccessToken(accessToken)).thenReturn(expectedAuthUser);

        ResourceOwner actualAuthUser = subject.getByAccessToken(accessToken);
        assertThat(actualAuthUser, is(expectedAuthUser));
    }

    @Test
    public void insert() {
        ResourceOwner authUser = FixtureFactory.makeResourceOwner();
        subject.insert(authUser);
        verify(mockMapper, times(1)).insert(authUser);
    }
}
