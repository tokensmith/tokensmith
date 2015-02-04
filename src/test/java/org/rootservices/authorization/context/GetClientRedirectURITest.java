package org.rootservices.authorization.context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetClientRedirectURITest {

    @Mock
    private ClientRepository mockClientRepository;

    private GetClientRedirectURI subject;

    @Before
    public void setUp() {
        subject = new GetClientRedirectURIImpl();
        ReflectionTestUtils.setField(subject, "clientRepository", mockClientRepository);
    }

    @Test
    public void run() throws RecordNotFoundException, URISyntaxException {
        UUID uuid = UUID.randomUUID();
        URI expected = new URI("https://rootservices.org");
        Client client = new Client();
        client.setRedirectURI(expected);
        when(mockClientRepository.getByUUID(uuid)).thenReturn(client);

        URI actual = subject.run(uuid);
        assertThat(actual).isEqualTo(expected);

    }
}