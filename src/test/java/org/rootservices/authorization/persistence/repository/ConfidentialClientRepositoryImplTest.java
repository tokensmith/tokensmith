package org.rootservices.authorization.persistence.repository;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.ConfidentialClientMapper;

import java.net.URISyntaxException;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 5/24/15.
 */
public class ConfidentialClientRepositoryImplTest {

    @Mock
    private ConfidentialClientMapper mockConfidentialClientMapper;

    private ConfidentialClientRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new ConfidentialClientRepositoryImpl(mockConfidentialClientMapper);
    }

    @Test
    public void insert() throws URISyntaxException {
        Client client = FixtureFactory.makeCodeClientWithScopes();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);
        subject.insert(confidentialClient);

        verify(mockConfidentialClientMapper, times(1)).insert(confidentialClient);
    }

    @Test
    public void getByClientUUID() throws URISyntaxException, RecordNotFoundException {
        Client client = FixtureFactory.makeCodeClientWithScopes();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);
        when(mockConfidentialClientMapper.getByClientId(confidentialClient.getClient().getId())).thenReturn(confidentialClient);

        ConfidentialClient actual = subject.getByClientId(confidentialClient.getClient().getId());
        assertThat(actual).isEqualTo(confidentialClient);
    }

    @Test(expected = RecordNotFoundException.class)
    public void getByClientUUIDRecordNotFound() throws RecordNotFoundException {
        UUID clientUUID = UUID.randomUUID();
        when(mockConfidentialClientMapper.getByClientId(clientUUID)).thenReturn(null);

        subject.getByClientId(clientUUID);
    }
}