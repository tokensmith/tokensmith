package org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.context;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.ResponseTypeException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 8/12/16.
 */
public class GetOpenIdPublicClientRedirectUriTest {
    @Mock
    private ClientRepository mockClientRepository;
    private GetOpenIdPublicClientRedirectUri subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new GetOpenIdPublicClientRedirectUri(mockClientRepository);
    }

    @Test
    public void clientFoundRedirectMatchesShouldBeOK() throws URISyntaxException, RecordNotFoundException, InformClientException, InformResourceOwnerException {

        Client client = FixtureFactory.makeTokenClientWithOpenIdScopes();

        when(mockClientRepository.getById(client.getId())).thenReturn(client);

        ResponseTypeException rootCause = new ResponseTypeException("");

        boolean actual = subject.run(client.getId(), client.getRedirectURI(), rootCause);
        assertThat(actual, is(true));
    }

    @Test
    public void clientNotFoundShouldThrowInformResourceOwnerException() throws RecordNotFoundException, URISyntaxException {
        UUID clientId = UUID.randomUUID();
        URI redirectURI = new URI("https://rootservices.org");
        ResponseTypeException rootCause = new ResponseTypeException("");

        when(mockClientRepository.getById(clientId)).thenThrow(RecordNotFoundException.class);

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

        URI redirectURI = new URI("https://rootservices.org/mismatch");
        ResponseTypeException rootCause = new ResponseTypeException("");

        Client client = FixtureFactory.makeTokenClientWithOpenIdScopes();

        when(mockClientRepository.getById(client.getId())).thenReturn(client);

        try {
            subject.run(client.getId(), redirectURI, rootCause);
            fail("InformResourceOwnerException expected");
        } catch(InformClientException e) {
            fail("InformResourceOwnerException expected");
        } catch(InformResourceOwnerException e) {
            assertThat(e.getDomainCause() instanceof ResponseTypeException, is(true));
            assertThat(e.getCode(), is(ErrorCode.REDIRECT_URI_MISMATCH.getCode()));
        }
    }

}