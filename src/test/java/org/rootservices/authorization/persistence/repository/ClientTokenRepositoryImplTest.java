package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.ClientToken;
import org.rootservices.authorization.persistence.mapper.ClientTokenMapper;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 10/2/16.
 */
public class ClientTokenRepositoryImplTest {
    private ClientTokenRepository subject;
    @Mock
    private ClientTokenMapper mockClientTokenMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new ClientTokenRepositoryImpl(mockClientTokenMapper);

    }
    @Test
    public void testInsert() throws Exception {
        ClientToken clientToken = new ClientToken();
        subject.insert(clientToken);
        verify(mockClientTokenMapper, times(1)).insert(clientToken);
    }
}