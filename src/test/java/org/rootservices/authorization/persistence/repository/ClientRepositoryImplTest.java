package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.ClientMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 11/16/14.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientRepositoryImplTest {

    @Mock
    private ClientMapper mockMapper;

    private ClientRepository subject;

    @Before
    public void setUp() {
        subject = new ClientRepositoryImpl(mockMapper);
    }

    public Client clientBuilder() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();
        ResponseType rt = ResponseType.CODE;
        URI redirectUri = new URI("https://rootservices.org");
        Client client = new Client(uuid, rt, redirectUri);
        return client;
    }

    @Test(expected= RecordNotFoundException.class)
    public void getByUUIDNoRecordFound() throws RecordNotFoundException{
        UUID uuid = UUID.randomUUID();
        when(mockMapper.getByUUID(uuid)).thenReturn(null);
        subject.getByUUID(uuid);
    }

    @Test
    public void getByUUID() throws RecordNotFoundException, URISyntaxException{
        Client expectedClient = clientBuilder();
        when(mockMapper.getByUUID(expectedClient.getUuid())).thenReturn(expectedClient);
        Client actualClient = subject.getByUUID(expectedClient.getUuid());
        assertThat(actualClient).isEqualTo(expectedClient);
    }
}
