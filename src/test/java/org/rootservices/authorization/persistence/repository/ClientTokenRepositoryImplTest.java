package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.ClientToken;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.ClientTokenMapper;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void insertShouldBeOk() throws Exception {
        ClientToken clientToken = new ClientToken();
        subject.insert(clientToken);
        verify(mockClientTokenMapper, times(1)).insert(clientToken);
    }

    @Test
    public void getByTokenIdShouldBeOk() throws Exception {
        UUID clientId = UUID.randomUUID();
        ClientToken clientToken = new ClientToken();

        when(mockClientTokenMapper.getByTokenId(clientId)).thenReturn(clientToken);

        ClientToken actual = subject.getByTokenId(clientId);

        assertThat(actual, is(clientToken));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getByTokenIdShouldThrowRecordNotFound() throws Exception {
        UUID clientId = UUID.randomUUID();
        when(mockClientTokenMapper.getByTokenId(clientId)).thenReturn(null);

        subject.getByTokenId(clientId);
    }
}