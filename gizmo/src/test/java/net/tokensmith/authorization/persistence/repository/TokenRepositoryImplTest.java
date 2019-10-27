package net.tokensmith.authorization.persistence.repository;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.authorization.persistence.entity.Token;
import net.tokensmith.authorization.persistence.exceptions.DuplicateRecordException;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.factory.DuplicateRecordExceptionFactory;
import net.tokensmith.authorization.persistence.mapper.TokenMapper;
import org.springframework.dao.DuplicateKeyException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;


import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


public class TokenRepositoryImplTest {

    @Mock
    private TokenMapper mockTokenMapper;
    @Mock
    private DuplicateRecordExceptionFactory mockDuplicateRecordExceptionFactory;

    private TokenRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new TokenRepositoryImpl(mockTokenMapper, mockDuplicateRecordExceptionFactory);
    }

    @Test
    public void insert() throws DuplicateRecordException {
        UUID clientId = UUID.randomUUID();
        String accessToken = "access-token";
        Token token = FixtureFactory.makeOpenIdToken(accessToken, clientId, new ArrayList<>());
        subject.insert(token);
        verify(mockTokenMapper, times(1)).insert(token);
    }

    @Test
    public void insertDuplicateTokenShouldThrowDuplicateRecordException() throws DuplicateRecordException {
        UUID clientId = UUID.randomUUID();
        String accessToken = "access-token";
        Token token = FixtureFactory.makeOpenIdToken(accessToken, clientId, new ArrayList<>());

        String msg =
        "### Error updating database.  Cause: org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"token_unique\"\n" +
        "Detail: Key (token)=(\\x243261243130246f424b7059744e4f594c57496c5a484258552f566865497843673249577244476d474b504b4e316e59614378657a466b342e314b69) already exists.\n" +
        "### The error may involve defaultParameterMap\n" +
        "### The error occurred while setting parameters\n" +
        "### SQL: insert into token (id, token, grant_type, expires_at)         values (             ?,             ?,             ?,             ?         )\n" +
        "### Cause: org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"token_unique\"\n" +
        "Detail: Key (token)=(\\x243261243130246f424b7059744e4f594c57496c5a484258552f566865497843673249577244476d474b504b4e316e59614378657a466b342e314b69) already exists.\n" +
        "; SQL []; ERROR: duplicate key value violates unique constraint \"token_unique\"\n" +
        "Detail: Key (token)=(\\x243261243130246f424b7059744e4f594c57496c5a484258552f566865497843673249577244476d474b504b4e316e59614378657a466b342e314b69) already exists.; nested exception is org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"token_unique\"\n" +
        "Detail: Key (token)=(\\x243261243130246f424b7059744e4f594c57496c5a484258552f566865497843673249577244476d474b504b4e316e59614378657a466b342e314b69) already exists.\"";

        DuplicateKeyException dke = new DuplicateKeyException(msg);
        doThrow(dke).when(mockTokenMapper).insert(token);

        DuplicateRecordException dre = new DuplicateRecordException("message", dke);
        when(mockDuplicateRecordExceptionFactory.make(dke, "token")).thenReturn(dre);

        DuplicateRecordException actual = null;
        try {
            subject.insert(token);
        } catch (DuplicateRecordException e) {
            actual = e;
        }
        assertThat(actual, is(dre));
    }

    @Test
    public void revokeByAuthCodeIdShouldBeOk() {
        UUID authCodeId = UUID.randomUUID();

        subject.revokeByAuthCodeId(authCodeId);
        verify(mockTokenMapper).revokeByAuthCodeId(authCodeId);
    }

    @Test
    public void revokeByIdShouldBeOk() {
        UUID id = UUID.randomUUID();

        subject.revokeById(id);
        verify(mockTokenMapper).revokeById(id);
    }

    @Test
    public void getByAuthCodeIdShouldBeOk() throws Exception {
        Token token = new Token();
        UUID authCodeId = UUID.randomUUID();
        when(mockTokenMapper.getByAuthCodeId(authCodeId)).thenReturn(token);

        Token actual = subject.getByAuthCodeId(authCodeId);
        assertThat(actual, is(token));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getByAuthCodeIdShouldThrowRecordNotFound() throws Exception {

        UUID authCodeId = UUID.randomUUID();
        when(mockTokenMapper.getByAuthCodeId(authCodeId)).thenReturn(null);

        subject.getByAuthCodeId(authCodeId);
    }

    @Test
    public void updateExpiresAtByAccessTokenShouldBeOk() {
        String accessToken = "access-token";
        OffsetDateTime expiresAt = OffsetDateTime.now();

        subject.updateExpiresAtByAccessToken(expiresAt, accessToken);

        verify(mockTokenMapper, times(1)).updateExpiresAtByAccessToken(expiresAt, accessToken);
    }

    @Test
    public void revokeActiveShouldBeOk() {
        UUID resourceOwnerId = UUID.randomUUID();
        subject.revokeActive(resourceOwnerId);

        verify(mockTokenMapper, times(1)).revokeActive(resourceOwnerId);
    }
}