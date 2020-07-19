package net.tokensmith.authorization.persistence.repository;


import net.tokensmith.authorization.persistence.factory.DuplicateRecordExceptionFactory;
import net.tokensmith.authorization.persistence.mapper.LocalTokenMapper;
import net.tokensmith.repository.entity.LocalToken;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.LocalTokenRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocalTokenRepoTest {

    @Mock
    private LocalTokenMapper mockLocalTokenMapper;
    @Mock
    private DuplicateRecordExceptionFactory mockDuplicateRecordExceptionFactory;

    private LocalTokenRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new LocalTokenRepo(mockLocalTokenMapper, mockDuplicateRecordExceptionFactory);
    }

    public LocalToken localToken() {
        return new LocalToken(
            UUID.randomUUID(), "local-token", UUID.randomUUID(), OffsetDateTime.now().plusDays(1)
        );
    }

    @Test
    public void insert() throws DuplicateRecordException {
        LocalToken localToken = localToken();

        subject.insert(localToken);
        verify(mockLocalTokenMapper).insert(localToken);
    }

    @Test
    public void insertDuplicateLocalTokenShouldThrowDuplicateRecordException() throws DuplicateRecordException {
        LocalToken localToken = localToken();

        StringBuilder msg = new StringBuilder()
            .append("### Error updating database.  Cause: org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"local_token_active_token_unique\"")
            .append("\n")
            .append("Detail: Key (active_token)=(local-token) already exists.")
            .append("\n")
            .append("### The error may exist in net/tokensmith/authorization/persistence/mapper/LocalTokenMapper.xml")
            .append("\n")
            .append("### The error may involve defaultParameterMap")
            .append("\n")
            .append("### The error occurred while setting parameters")
            .append("\n")
            .append("### SQL: insert into local_token (id, active_token, resource_owner_id, expires_at)         values (         ?,         ?,         ?,         ?         )")
            .append("\n")
            .append("### Cause: org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"local_token_active_token_unique\"")
            .append("\n")
            .append("Detail: Key (active_token)=(local-token) already exists.")
            .append("\n")
            .append("\"\"; ERROR: duplicate key value violates unique constraint \"local_token_active_token_unique\"")
            .append("\n")
            .append("Detail: Key (active_token)=(local-token) already exists.; nested exception is org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"local_token_active_token_unique\"")
            .append("\n")
            .append("Detail: Key (active_token)=(local-token) already exists.");

        DuplicateKeyException dke = new DuplicateKeyException(msg.toString());
        doThrow(dke).when(mockLocalTokenMapper).insert(localToken);

        DuplicateRecordException dre = new DuplicateRecordException("message", dke);
        when(mockDuplicateRecordExceptionFactory.make(dke, "local_token")).thenReturn(dre);

        DuplicateRecordException actual = null;
        try {
            subject.insert(localToken);
        } catch (DuplicateRecordException e) {
            actual = e;
        }
        assertThat(actual, is(dre));
    }

    @Test
    public void getByIdShouldReturnBeOK() throws RecordNotFoundException {
        LocalToken localToken = localToken();

        when(mockLocalTokenMapper.getById(localToken.getId())).thenReturn(localToken);

        LocalToken actual = subject.getById(localToken.getId());
        assertThat(actual, is(localToken));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getByIdShouldThrowRecordNotFoundException() throws RecordNotFoundException {
        UUID id = UUID.randomUUID();

        when(subject.getById(id)).thenReturn(null);

        subject.getById(id);
    }

    @Test
    public void revokeActive() {
        UUID resourceOwnerId = UUID.randomUUID();

        subject.revokeActive(resourceOwnerId);
        verify(mockLocalTokenMapper).revokeActive(resourceOwnerId);
    }
}