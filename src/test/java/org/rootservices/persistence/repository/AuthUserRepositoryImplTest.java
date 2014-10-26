package org.rootservices.persistence.repository;

import org.rootservices.persistence.entity.AuthUser;
import org.rootservices.persistence.exceptions.RecordNotFoundException;
import org.rootservices.persistence.mapper.AuthUserMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 10/11/14.
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthUserRepositoryImplTest {

    @Mock
    AuthUserMapper mockAuthUserMapper;

    AuthUserRepositoryImpl subject;

    @Before
    public void setUp() {
        subject = new AuthUserRepositoryImpl();
        ReflectionTestUtils.setField(subject, "authUserMapper", mockAuthUserMapper);
    }

    public AuthUser authUserBuilder() {
        UUID uuid = UUID.randomUUID();
        byte [] password = "plainTextPassword".getBytes();
        AuthUser authUser = new AuthUser(uuid, "test@tommygunz.com", password);
        return authUser;
    }

    @Test(expected= RecordNotFoundException.class)
    public void getByUUIDNoRecordFound() throws RecordNotFoundException{
        UUID uuid = UUID.randomUUID();
        when(mockAuthUserMapper.getByUUID(uuid)).thenReturn(null);
        subject.getByUUID(uuid);
    }

    @Test
    public void getByUUID() throws RecordNotFoundException{

        AuthUser expectedAuthUser = authUserBuilder();

        when(mockAuthUserMapper.getByUUID(expectedAuthUser.getUuid())).thenReturn(expectedAuthUser);
        AuthUser actualAuthUser = subject.getByUUID(expectedAuthUser.getUuid());
        assertThat(actualAuthUser).isEqualTo(expectedAuthUser);
    }

    @Test(expected= RecordNotFoundException.class)
    public void getByEmailAndPasswordNoRecordFound() throws RecordNotFoundException{
        String email = "test@tommygunz.com";
        byte [] password = "plainTextPassword".getBytes();

        when(mockAuthUserMapper.getByEmailAndPassword(email, password)).thenReturn(null);
        subject.getByEmailAndPassword(email, password);
    }

    @Test
    public void getByEmailAndPassword() throws RecordNotFoundException{

        AuthUser expectedAuthUser = authUserBuilder();

        when(mockAuthUserMapper.getByEmailAndPassword(
                expectedAuthUser.getEmail(), expectedAuthUser.getPassword())
        ).thenReturn(expectedAuthUser);

        AuthUser actualAuthUser = subject.getByEmailAndPassword(
                expectedAuthUser.getEmail(),
                expectedAuthUser.getPassword()
        );
        assertThat(actualAuthUser).isEqualTo(expectedAuthUser);
    }

    @Test
    public void update() {
        AuthUser authUser = authUserBuilder();
        subject.update(authUser);
        verify(mockAuthUserMapper, times(1)).update(authUser);
    }

    @Test
    public void insert() {
        AuthUser authUser = authUserBuilder();
        subject.insert(authUser);
        verify(mockAuthUserMapper, times(1)).insert(authUser);
    }

}
