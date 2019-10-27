package net.tokensmith.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.authorization.persistence.entity.AuthCode;
import net.tokensmith.authorization.persistence.exceptions.DuplicateRecordException;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.factory.DuplicateRecordExceptionFactory;
import net.tokensmith.authorization.persistence.mapper.AuthCodeMapper;
import org.springframework.dao.DuplicateKeyException;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 4/10/15.
 */
public class AuthCodeRepositoryImplTest {

    @Mock
    private AuthCodeMapper mockMapper;
    @Mock
    private DuplicateRecordExceptionFactory mockDuplicateRecordExceptionFactory;

    private AuthCodeRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new AuthCodeRepositoryImpl(mockDuplicateRecordExceptionFactory, mockMapper);
    }

    @Test
    public void testInsert() throws DuplicateRecordException {
        AuthCode authCode = new AuthCode();
        subject.insert(authCode);
        verify(mockMapper).insert(authCode);
    }

    @Test
    public void testInsertDuplicateExpectDuplicateRecordException() throws Exception {

        AuthCode authCode = new AuthCode();
        String msg = "some error message from the db.";
        DuplicateKeyException dke = new DuplicateKeyException(msg);
        doThrow(dke).when(mockMapper).insert(authCode);

        DuplicateRecordException dre = new DuplicateRecordException("message", dke);
        when(mockDuplicateRecordExceptionFactory.make(dke, "auth_code")).thenReturn(dre);

        DuplicateRecordException actual = null;
        try {
            subject.insert(authCode);
        } catch (DuplicateRecordException e) {
            actual = e;
        }
        assertThat(actual, is(dre));
    }

    @Test
    public void getByClientIdAndAuthCodeShouldBeOk() throws RecordNotFoundException {
        UUID clientUUID = UUID.randomUUID();
        String code = "authorization-code";
        AuthCode expected = new AuthCode();
        when(mockMapper.getByClientIdAndAuthCode(clientUUID, code)).thenReturn(expected);

        AuthCode actual = subject.getByClientIdAndAuthCode(clientUUID, code);
        assertThat(actual, is(expected));
    }


    @Test(expected = RecordNotFoundException.class)
    public void getByClientIdAndAuthCodeShouldThrowRecordNotFound() throws RecordNotFoundException {
        UUID clientUUID = UUID.randomUUID();
        String code = "authorization-code";
        when(mockMapper.getByClientIdAndAuthCode(clientUUID, code)).thenReturn(null);

        subject.getByClientIdAndAuthCode(clientUUID, code);
    }

    @Test
    public void getByIdShouldBeOk() throws Exception {
        UUID id = UUID.randomUUID();
        AuthCode authCode = new AuthCode();

        when(mockMapper.getById(id)).thenReturn(authCode);
        AuthCode actual = subject.getById(id);

        assertThat(actual, is(authCode));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getByIdShouldThrowRecordNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(mockMapper.getById(id)).thenReturn(null);
        subject.getById(id);
    }

    @Test
    public void revokeByIdShouldBeOk() {
        UUID id = UUID.randomUUID();

        subject.revokeById(id);
        verify(mockMapper, times(1)).revokeById(id);
    }
}