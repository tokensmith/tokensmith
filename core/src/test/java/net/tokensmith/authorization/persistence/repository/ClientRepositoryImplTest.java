package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.repo.ClientRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.mapper.ClientMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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
        URI redirectUri = new URI("https://rootservices.org");
        Client client = new Client(uuid, redirectUri);
        return client;
    }

    @Test(expected= RecordNotFoundException.class)
    public void getByUUIDNoRecordFound() throws RecordNotFoundException{
        UUID uuid = UUID.randomUUID();
        when(mockMapper.getById(uuid)).thenReturn(null);
        subject.getById(uuid);
    }

    @Test
    public void getByUUID() throws RecordNotFoundException, URISyntaxException{
        Client expectedClient = clientBuilder();
        when(mockMapper.getById(expectedClient.getId())).thenReturn(expectedClient);
        Client actualClient = subject.getById(expectedClient.getId());
        assertThat(actualClient, is(expectedClient));
    }
}
