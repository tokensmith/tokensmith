package org.rootservices.authorization.oauth2.grant.code.authorization.request;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.GetClientRedirect;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.GetClientRedirectImpl;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.exception.ResponseTypeException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import static org.mockito.Mockito.when;

public class GetClientRedirectImplTest {

    @Mock
    private ClientRepository mockClientRepository;

    private GetClientRedirect subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new GetClientRedirectImpl(mockClientRepository);
    }

    @Test
    public void clientNotFound() throws RecordNotFoundException, URISyntaxException {
        UUID clientId = UUID.randomUUID();
        Optional<URI> redirectURI = Optional.ofNullable(new URI("https://rootservices.org"));
        ResponseTypeException rootCause = new ResponseTypeException("");

        when(mockClientRepository.getByUUID(clientId)).thenThrow(RecordNotFoundException.class);

        try {
            subject.run(clientId, redirectURI, rootCause);
            fail("InformResourceOwnerException expected");
        } catch(InformClientException e) {
            fail("InformResourceOwnerException expected");
        } catch(InformResourceOwnerException e) {
            assertThat(e.getDomainCause() instanceof RecordNotFoundException).isTrue();
            assertThat(e.getCode()).isEqualTo(ErrorCode.CLIENT_NOT_FOUND.getCode());
        }
    }

    @Test
    public void clientFoundRedirectUriMismatch() throws RecordNotFoundException, URISyntaxException {
        UUID clientId = UUID.randomUUID();
        Optional<URI> redirectURI = Optional.ofNullable(new URI("https://rootservices.org"));
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
            assertThat(e.getDomainCause() instanceof ResponseTypeException).isTrue();
            assertThat(e.getCode()).isEqualTo(ErrorCode.REDIRECT_URI_MISMATCH.getCode());
        }
    }


    @Test
    public void clientFoundRedirectUriIsNotPresent() throws RecordNotFoundException, URISyntaxException {
        UUID clientId = UUID.randomUUID();
        Optional<URI> redirectURI = Optional.empty();
        ResponseTypeException rootCause = new ResponseTypeException("");

        URI expectedURI = new URI("https://rootservices.org/continue");
        Client client = new Client();
        client.setUuid(clientId);
        client.setRedirectURI(expectedURI);

        when(mockClientRepository.getByUUID(clientId)).thenReturn(client);

        URI actualURI = null;
        try {
            actualURI = subject.run(clientId, redirectURI, rootCause);
        } catch(InformClientException e) {
            fail("No exception expected");
        } catch(InformResourceOwnerException e) {
            fail("No exception expected");
        }

        assertThat(actualURI).isEqualTo(expectedURI);
    }

    @Test
    public void clientFoundRedirectUrisMatch() throws RecordNotFoundException, URISyntaxException {
        UUID clientId = UUID.randomUUID();
        URI expectedURI = new URI("https://rootservices.org");
        Optional<URI> redirectURI = Optional.ofNullable(expectedURI);
        ResponseTypeException rootCause = new ResponseTypeException("");

        Client client = new Client();
        client.setUuid(clientId);
        client.setRedirectURI(expectedURI);

        when(mockClientRepository.getByUUID(clientId)).thenReturn(client);

        URI actualURI = null;
        try {
            actualURI = subject.run(clientId, redirectURI, rootCause);
        } catch(InformClientException|InformResourceOwnerException e) {
            fail("caught: " + e.getClass() + " with code: " + e.getCode() + " when no exception was expected");
        }

        assertThat(actualURI).isEqualTo(expectedURI);
    }
}