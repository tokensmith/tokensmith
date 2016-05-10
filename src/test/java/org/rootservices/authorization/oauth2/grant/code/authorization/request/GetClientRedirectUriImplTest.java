package org.rootservices.authorization.oauth2.grant.code.authorization.request;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.context.GetClientRedirectUri;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.context.GetClientRedirectUriImpl;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.exception.ResponseTypeException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ConfidentialClientRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class GetClientRedirectUriImplTest {

    @Mock
    private ConfidentialClientRepository mockConfidentialClientRepository;

    private GetClientRedirectUri subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new GetClientRedirectUriImpl(mockConfidentialClientRepository);
    }

    @Test
    public void clientNotFound() throws RecordNotFoundException, URISyntaxException {
        UUID clientId = UUID.randomUUID();
        Optional<URI> redirectURI = Optional.ofNullable(new URI("https://rootservices.org"));
        ResponseTypeException rootCause = new ResponseTypeException("");

        when(mockConfidentialClientRepository.getByClientId(clientId)).thenThrow(RecordNotFoundException.class);

        try {
            subject.run(clientId, redirectURI, rootCause);
            fail("InformResourceOwnerException expected");
        } catch(InformClientException e) {
            fail("InformResourceOwnerException expected");
        } catch(InformResourceOwnerException e) {
            assertThat(e.getDomainCause(), instanceOf(RecordNotFoundException.class));
            assertThat(e.getCode(), is(ErrorCode.CLIENT_NOT_FOUND.getCode()));
        }
    }

    @Test
    public void clientFoundRedirectUriMismatch() throws RecordNotFoundException, URISyntaxException {

        Client client = FixtureFactory.makeCodeClientWithScopes();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);
        when(mockConfidentialClientRepository.getByClientId(client.getUuid())).thenReturn(confidentialClient);

        Optional<URI> redirectURI = Optional.ofNullable(new URI("https://rootservices.org/will/not/match"));
        ResponseTypeException rootCause = new ResponseTypeException("");

        try {
            subject.run(client.getUuid(), redirectURI, rootCause);
            fail("InformResourceOwnerException expected");
        } catch(InformClientException e) {
            fail("InformResourceOwnerException expected");
        } catch(InformResourceOwnerException e) {
            assertThat(e.getDomainCause(), instanceOf(ResponseTypeException.class));
            assertThat(e.getCode(), is(ErrorCode.REDIRECT_URI_MISMATCH.getCode()));
        }
    }


    @Test
    public void clientFoundRedirectUriIsNotPresent() throws RecordNotFoundException, URISyntaxException {

        Client client = FixtureFactory.makeCodeClientWithScopes();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);
        when(mockConfidentialClientRepository.getByClientId(client.getUuid())).thenReturn(confidentialClient);

        Optional<URI> redirectURI = Optional.empty();
        ResponseTypeException rootCause = new ResponseTypeException("");

        URI actual = null;
        try {
            actual = subject.run(client.getUuid(), redirectURI, rootCause);
        } catch(InformClientException e) {
            fail("No exception expected");
        } catch(InformResourceOwnerException e) {
            fail("No exception expected");
        }

        assertThat(actual, is(client.getRedirectURI()));
    }

    @Test
    public void clientFoundRedirectUrisMatch() throws RecordNotFoundException, URISyntaxException {

        Client client = FixtureFactory.makeCodeClientWithScopes();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);
        when(mockConfidentialClientRepository.getByClientId(client.getUuid())).thenReturn(confidentialClient);

        ResponseTypeException rootCause = new ResponseTypeException("");
        Optional<URI> redirectUri = Optional.of(client.getRedirectURI());

        URI actual = null;
        try {
            actual = subject.run(client.getUuid(), redirectUri, rootCause);
        } catch(InformClientException|InformResourceOwnerException e) {
            fail("caught: " + e.getClass() + " with code: " + e.getCode() + " when no exception was expected");
        }

        assertThat(actual, is(client.getRedirectURI()));
    }
}