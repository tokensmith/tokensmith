package org.rootservices.authorization.oauth2.grant.redirect.code.authorization.request.context;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.context.GetClientRedirectUri;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.parse.exception.OptionalException;
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

public class GetConfidentialClientRedirectUriTest {

    @Mock
    private ConfidentialClientRepository mockConfidentialClientRepository;

    private GetClientRedirectUri subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new GetConfidentialClientRedirectUri(mockConfidentialClientRepository);
    }

    @Test
    public void clientNotFound() throws RecordNotFoundException, URISyntaxException {
        UUID clientId = UUID.randomUUID();
        Optional<URI> redirectURI = Optional.ofNullable(new URI("https://rootservices.org"));
        Exception rootCause = new OptionalException();

        when(mockConfidentialClientRepository.getByClientId(clientId)).thenThrow(RecordNotFoundException.class);

        try {
            subject.run(clientId, redirectURI, rootCause);
            fail("InformResourceOwnerException expected");
        } catch(InformResourceOwnerException e) {
            assertThat(e.getCause(), instanceOf(RecordNotFoundException.class));
            assertThat(e.getCode(), is(ErrorCode.CLIENT_NOT_FOUND.getCode()));
        }
    }

    @Test
    public void clientFoundRedirectUriMismatch() throws RecordNotFoundException, URISyntaxException {

        Client client = FixtureFactory.makeCodeClientWithScopes();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);
        when(mockConfidentialClientRepository.getByClientId(client.getId())).thenReturn(confidentialClient);

        Optional<URI> redirectURI = Optional.ofNullable(new URI("https://rootservices.org/will/not/match"));
        Exception rootCause = new OptionalException();

        try {
            subject.run(client.getId(), redirectURI, rootCause);
            fail("InformResourceOwnerException expected");
        } catch(InformResourceOwnerException e) {
            assertThat(e.getCause(), instanceOf(OptionalException.class));
            assertThat(e.getCode(), is(ErrorCode.REDIRECT_URI_MISMATCH.getCode()));
        }
    }


    @Test
    public void clientFoundRedirectUriIsNotPresent() throws RecordNotFoundException, URISyntaxException {

        Client client = FixtureFactory.makeCodeClientWithScopes();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);
        when(mockConfidentialClientRepository.getByClientId(client.getId())).thenReturn(confidentialClient);

        Optional<URI> redirectURI = Optional.empty();
        Exception rootCause = new OptionalException();

        URI actual = null;
        try {
            actual = subject.run(client.getId(), redirectURI, rootCause);
        } catch(InformResourceOwnerException e) {
            fail("No exception expected");
        }

        assertThat(actual, is(client.getRedirectURI()));
    }

    @Test
    public void clientFoundRedirectUrisMatch() throws RecordNotFoundException, URISyntaxException {

        Client client = FixtureFactory.makeCodeClientWithScopes();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);
        when(mockConfidentialClientRepository.getByClientId(client.getId())).thenReturn(confidentialClient);

        Exception rootCause = new OptionalException();
        Optional<URI> redirectUri = Optional.of(client.getRedirectURI());

        URI actual = null;
        try {
            actual = subject.run(client.getId(), redirectUri, rootCause);
        } catch(InformResourceOwnerException e) {
            fail("caught: " + e.getClass() + " with code: " + e.getCode() + " when no exception was expected");
        }

        assertThat(actual, is(client.getRedirectURI()));
    }
}