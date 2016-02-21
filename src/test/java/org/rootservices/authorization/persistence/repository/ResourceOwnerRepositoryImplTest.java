package org.rootservices.authorization.persistence.repository;

import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.ResourceOwnerMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
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

    public ResourceOwner makeAuthUser() {
        UUID uuid = UUID.randomUUID();
        byte [] password = "plainTextPassword".getBytes();
        ResourceOwner authUser = new ResourceOwner(uuid, "test@rootservices.org", password);
        return authUser;
    }

    @Test(expected= RecordNotFoundException.class)
    public void getByUUIDNoRecordFound() throws RecordNotFoundException{
        UUID uuid = UUID.randomUUID();
        when(mockMapper.getByUUID(uuid)).thenReturn(null);
        subject.getByUUID(uuid);
    }

    @Test
    public void getByUUID() throws RecordNotFoundException{

        ResourceOwner expectedAuthUser = makeAuthUser();

        when(mockMapper.getByUUID(expectedAuthUser.getUuid())).thenReturn(expectedAuthUser);
        ResourceOwner actualAuthUser = subject.getByUUID(expectedAuthUser.getUuid());
        assertThat(actualAuthUser).isEqualTo(expectedAuthUser);
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

        ResourceOwner expectedAuthUser = makeAuthUser();

        when(mockMapper.getByEmail(
                expectedAuthUser.getEmail())
        ).thenReturn(expectedAuthUser);

        ResourceOwner actualAuthUser = subject.getByEmail(
                expectedAuthUser.getEmail()
        );
        assertThat(actualAuthUser).isEqualTo(expectedAuthUser);
    }

    @Test
    public void insert() {
        ResourceOwner authUser = makeAuthUser();
        subject.insert(authUser);
        verify(mockMapper, times(1)).insert(authUser);
    }
}
