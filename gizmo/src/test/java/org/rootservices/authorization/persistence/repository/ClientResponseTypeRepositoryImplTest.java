package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.ClientResponseType;
import org.rootservices.authorization.persistence.mapper.ClientResponseTypeMapper;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 8/9/16.
 */
public class ClientResponseTypeRepositoryImplTest {
    @Mock
    private ClientResponseTypeMapper mockClientResponseTypeMapper;
    private ClientResponseTypeRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new ClientResponseTypeRepositoryImpl(mockClientResponseTypeMapper);
    }

    @Test
    public void testInsert() throws Exception {
        ClientResponseType clientResponseType = new ClientResponseType();

        subject.insert(clientResponseType);
        verify(mockClientResponseTypeMapper, times(1)).insert(clientResponseType);
    }
}