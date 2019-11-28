package net.tokensmith.authorization.persistence.repository;

import helper.fixture.FixtureFactory;
import org.hamcrest.core.Is;
import org.mockito.MockitoAnnotations;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.factory.DuplicateRecordExceptionFactory;
import net.tokensmith.authorization.persistence.mapper.ResourceOwnerMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.dao.DuplicateKeyException;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 10/11/14.
 */
public class ResourceOwnerRepoTest {

    @Mock
    private ResourceOwnerMapper mockMapper;
    @Mock
    private DuplicateRecordExceptionFactory mockDuplicateRecordExceptionFactory;

    private ResourceOwnerRepo subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new ResourceOwnerRepo(mockMapper, mockDuplicateRecordExceptionFactory);
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
        String email = "test@tokensmith.net";
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

    @Test(expected= RecordNotFoundException.class)
    public void getByAccessTokenWithProfileAndTokensShouldThrowRecordNotFound() throws RecordNotFoundException{
        String accessToken = "access-token";

        when(mockMapper.getByAccessTokenWithProfileAndTokens(accessToken)).thenReturn(null);
        subject.getByAccessTokenWithProfileAndTokens(accessToken);
    }

    @Test
    public void getByAccessTokenWithProfileAndTokensShouldBeOk() throws RecordNotFoundException{

        String accessToken = "access-token";
        ResourceOwner expectedAuthUser = FixtureFactory.makeResourceOwner();

        when(mockMapper.getByAccessTokenWithProfileAndTokens(accessToken)).thenReturn(expectedAuthUser);

        ResourceOwner actualAuthUser = subject.getByAccessTokenWithProfileAndTokens(accessToken);
        assertThat(actualAuthUser, is(expectedAuthUser));
    }

    @Test
    public void insert() throws Exception {
        ResourceOwner authUser = FixtureFactory.makeResourceOwner();
        subject.insert(authUser);
        verify(mockMapper, times(1)).insert(authUser);
    }


    @Test
    public void insertShouldThrowDuplicateRecordException() throws Exception {
        ResourceOwner user = FixtureFactory.makeResourceOwner();

        String msg =
            "### Error updating database.  Cause: org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"resource_owner_email_key\"\n" +
            "Detail: Key (email)=(test@rootservices.com) already exists.\n" +
            "### The error may involve defaultParameterMap\n" +
            "### The error occurred while setting parameters\n" +
            "### SQL: insert into resource_owner (id, email, password)         values (             ?,             ?,             ?         )\n" +
            "### Cause: org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"resource_owner_email_key\"\n" +
            "Detail: Key (email)=(test@rootservices.com) already exists.\n" +
            "; SQL []; ERROR: duplicate key value violates unique constraint \"resource_owner_email_key\"\n" +
            "Detail: Key (email)=(test@rootservices.com) already exists.; nested exception is org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"resource_owner_email_key\"\n" +
            "Detail: Key (email)=(test@rootservices.com) already exists\n";

        DuplicateKeyException dke = new DuplicateKeyException(msg);
        doThrow(dke).when(mockMapper).insert(user);

        DuplicateRecordException dre = new DuplicateRecordException(msg, dke);
        when(mockDuplicateRecordExceptionFactory.make(dke, "resource_owner")).thenReturn(dre);

        DuplicateRecordException actual = null;
        try {
            subject.insert(user);
        } catch (DuplicateRecordException e) {
            actual = e;
        }
        assertThat(actual, Is.is(dre));
    }

    @Test
    public void setEmailVerifiedShouldBeOk() {
        UUID id = UUID.randomUUID();
        subject.setEmailVerified(id);

        verify(mockMapper).setEmailVerified(id);
    }

    @Test
    public void updatePasswordShouldBeOk(){
        UUID id = UUID.randomUUID();
        String password = "plainTextPassword";

        subject.updatePassword(id, password);

        verify(mockMapper).updatePassword(id, password);
    }
}
