package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.mapper.AuthCodeMapper;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 4/10/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthCodeRepositoryImplTest {

    @Mock
    private AuthCodeMapper mockMapper;

    private AuthCodeRepository subject;

    @Before
    public void setUp() {
        subject = new AuthCodeRepositoryImpl(mockMapper);
    }

    @Test
    public void testInsert() throws Exception {
        AuthCode authCode = new AuthCode();
        subject.insert(authCode);
        verify(mockMapper).insert(authCode);
    }
}