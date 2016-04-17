package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.AuthCodeToken;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.mapper.AuthCodeTokenMapper;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 4/16/16.
 */
public class AuthCodeTokenRepositoryImplTest {

    @Mock
    private AuthCodeTokenMapper mockAuthCodeTokenMapper;

    private AuthCodeTokenRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new AuthCodeTokenRepositoryImpl(mockAuthCodeTokenMapper);
    }

    @Test
    public void insertShouldBeOk() throws Exception {
        AuthCodeToken authCodeToken = new AuthCodeToken();

        subject.insert(authCodeToken);

        verify(mockAuthCodeTokenMapper, times(1)).insert(authCodeToken);
    }

    @Test(expected = DuplicateRecordException.class)
    public void insertDuplicateAuthCodeId() throws DuplicateRecordException {
        AuthCodeToken authCodeToken = new AuthCodeToken();
        doThrow(org.springframework.dao.DuplicateKeyException.class).when(mockAuthCodeTokenMapper).insert(authCodeToken);

        subject.insert(authCodeToken);
    }
}