package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.ResourceOwnerMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 10/11/14.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceOwnerRepositoryImplTest {

    @Mock
    private ResourceOwnerMapper mockMapper;

    private ResourceOwnerRepositoryImpl subject;

    @Before
    public void setUp() {
        subject = new ResourceOwnerRepositoryImpl(mockMapper);
    }

    public ResourceOwner authUserBuilder() {
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

        ResourceOwner expectedAuthUser = authUserBuilder();

        when(mockMapper.getByUUID(expectedAuthUser.getUuid())).thenReturn(expectedAuthUser);
        ResourceOwner actualAuthUser = subject.getByUUID(expectedAuthUser.getUuid());
        assertThat(actualAuthUser).isEqualTo(expectedAuthUser);
    }

    @Test(expected= RecordNotFoundException.class)
    public void getByEmailAndPasswordNoRecordFound() throws RecordNotFoundException{
        String email = "test@rootservices.org";
        byte [] password = "plainTextPassword".getBytes();

        when(mockMapper.getByEmailAndPassword(email, password)).thenReturn(null);
        subject.getByEmailAndPassword(email, password);
    }

    @Test
    public void getByEmailAndPassword() throws RecordNotFoundException{

        ResourceOwner expectedAuthUser = authUserBuilder();

        when(mockMapper.getByEmailAndPassword(
                expectedAuthUser.getEmail(), expectedAuthUser.getPassword())
        ).thenReturn(expectedAuthUser);

        ResourceOwner actualAuthUser = subject.getByEmailAndPassword(
                expectedAuthUser.getEmail(),
                expectedAuthUser.getPassword()
        );
        assertThat(actualAuthUser).isEqualTo(expectedAuthUser);
    }

    @Test
    public void insert() {
        ResourceOwner authUser = authUserBuilder();
        subject.insert(authUser);
        verify(mockMapper, times(1)).insert(authUser);
    }

}
