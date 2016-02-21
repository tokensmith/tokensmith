package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.AuthCodeMapper;
import org.springframework.dao.DuplicateKeyException;

import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 4/10/15.
 */
public class AuthCodeRepositoryImplTest {

    @Mock
    private AuthCodeMapper mockMapper;

    private AuthCodeRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new AuthCodeRepositoryImpl(mockMapper);
    }

    @Test
    public void testInsert() throws DuplicateRecordException {
        AuthCode authCode = new AuthCode();
        subject.insert(authCode);
        verify(mockMapper).insert(authCode);
    }

    @Test(expected = DuplicateRecordException.class)
    public void testInsertDuplicateExpectDuplicateRecordException() throws DuplicateRecordException {
        AuthCode authCode = new AuthCode();
        Mockito.doThrow(new DuplicateKeyException("message")).when(mockMapper).insert(authCode);
        subject.insert(authCode);
    }

    @Test
    public void getByClientUUIDAndAuthCodeAndNotRevoked() throws RecordNotFoundException {
        UUID clientUUID = UUID.randomUUID();
        String code = "authorization-code";
        AuthCode expected = new AuthCode();
        when(mockMapper.getByClientUUIDAndAuthCodeAndNotRevoked(clientUUID, code)).thenReturn(expected);

        AuthCode actual = subject.getByClientUUIDAndAuthCodeAndNotRevoked(clientUUID, code);
        assertThat(actual).isEqualTo(expected);
    }


    @Test(expected = RecordNotFoundException.class)
    public void getByClientUUIDAndAuthCodeAndNotRevokedRecordNotFound() throws RecordNotFoundException {
        UUID clientUUID = UUID.randomUUID();
        String code = "authorization-code";
        when(mockMapper.getByClientUUIDAndAuthCodeAndNotRevoked(clientUUID, code)).thenReturn(null);

        subject.getByClientUUIDAndAuthCodeAndNotRevoked(clientUUID, code);
    }
}