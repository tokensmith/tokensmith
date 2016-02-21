package org.rootservices.authorization.grant.openid.protocol.authorization.request;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.ResponseTypeException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 10/8/15.
 */
public class CompareClientRedirectUriTest {
    @Mock
    private ClientRepository mockClientRepository;
    private CompareClientRedirectUri subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new CompareClientRedirectUriImpl(mockClientRepository);
    }

    @Test
    public void clientFoundRedirectMatchesShouldBeOK() throws URISyntaxException, RecordNotFoundException, InformClientException, InformResourceOwnerException {
        UUID clientId = UUID.randomUUID();
        URI redirectURI = new URI("https://rootservices.org");
        ResponseTypeException rootCause = new ResponseTypeException("");

        Client client = new Client();
        client.setUuid(clientId);
        client.setRedirectURI(redirectURI);

        when(mockClientRepository.getByUUID(clientId)).thenReturn(client);

        boolean actual = subject.run(clientId, redirectURI, rootCause);
        assertThat(actual, is(true));
    }

    @Test
    public void clientNotFoundShouldThrowInformResourceOwnerException() throws RecordNotFoundException, URISyntaxException {
        UUID clientId = UUID.randomUUID();
        URI redirectURI = new URI("https://rootservices.org");
        ResponseTypeException rootCause = new ResponseTypeException("");

        when(mockClientRepository.getByUUID(clientId)).thenThrow(RecordNotFoundException.class);

        try {
            subject.run(clientId, redirectURI, rootCause);
            fail("InformResourceOwnerException expected");
        } catch(InformClientException e) {
            fail("InformResourceOwnerException expected");
        } catch(InformResourceOwnerException e) {
            assertThat(e.getDomainCause() instanceof RecordNotFoundException, is(true));
            assertThat(e.getCode(), is(ErrorCode.CLIENT_NOT_FOUND.getCode()));
        }
    }

    @Test
    public void redirectUriMismatchShouldThrowInformResourceOwnerException() throws RecordNotFoundException, URISyntaxException {
        UUID clientId = UUID.randomUUID();
        URI redirectURI = new URI("https://rootservices.org");
        ResponseTypeException rootCause = new ResponseTypeException("");

        Client client = new Client();
        client.setUuid(clientId);
        client.setRedirectURI(new URI("https://rootservices.org/continue"));

        when(mockClientRepository.getByUUID(clientId)).thenReturn(client);

        try {
            subject.run(clientId, redirectURI, rootCause);
            fail("InformResourceOwnerException expected");
        } catch(InformClientException e) {
            fail("InformResourceOwnerException expected");
        } catch(InformResourceOwnerException e) {
            assertThat(e.getDomainCause() instanceof ResponseTypeException, is(true));
            assertThat(e.getCode(), is(ErrorCode.REDIRECT_URI_MISMATCH.getCode()));
        }
    }
}