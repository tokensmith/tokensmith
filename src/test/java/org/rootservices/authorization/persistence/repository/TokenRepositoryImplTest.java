package org.rootservices.authorization.persistence.repository;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.postgresql.util.PSQLException;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.factory.DuplicateRecordExceptionFactory;
import org.rootservices.authorization.persistence.mapper.TokenMapper;
import org.springframework.dao.DuplicateKeyException;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 5/23/15.
 */
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
        String accessToken = "access-token";
        Token token = FixtureFactory.makeOpenIdToken(accessToken);
        subject.insert(token);
        verify(mockTokenMapper, times(1)).insert(token);
    }

    @Test
    public void insertDuplicateTokenShouldThrowDuplicateRecordException() throws DuplicateRecordException {
        String accessToken = "access-token";
        Token token = FixtureFactory.makeOpenIdToken(accessToken);

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
}