package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.ClientMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 11/16/14.
 */
public class ClientRepositoryImplTest {

    @Mock
    private ClientMapper mockMapper;

    private ClientRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
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
        subject.getById(uuid);
    }

    @Test
    public void getByUUID() throws RecordNotFoundException, URISyntaxException{
        Client expectedClient = clientBuilder();
        when(mockMapper.getByUUID(expectedClient.getUuid())).thenReturn(expectedClient);
        Client actualClient = subject.getById(expectedClient.getUuid());
        assertThat(actualClient).isEqualTo(expectedClient);
    }
}
