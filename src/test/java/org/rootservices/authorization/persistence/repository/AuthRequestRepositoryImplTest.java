package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.persistence.entity.AuthRequest;
import org.rootservices.authorization.persistence.mapper.AuthRequestMapper;

import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 4/15/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthRequestRepositoryImplTest {

    @Mock
    private AuthRequestMapper mockAuthRequestMapper;

    private AuthRequestRepository subject;

    @Before
    public void setUp() {
        subject = new AuthRequestRepositoryImpl(mockAuthRequestMapper);
    }

    @Test
    public void insert() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        subject.insert(authRequest);
        verify(mockAuthRequestMapper).insert(authRequest);
    }
}