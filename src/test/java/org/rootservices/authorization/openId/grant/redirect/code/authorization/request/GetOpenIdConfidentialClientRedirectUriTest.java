package org.rootservices.authorization.openId.grant.redirect.code.authorization.request;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.ResponseTypeException;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.context.GetOpenIdConfidentialClientRedirectUri;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ConfidentialClientRepository;

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
public class GetOpenIdConfidentialClientRedirectUriTest {
    @Mock
    private ConfidentialClientRepository mockConfidentialClientRepository;
    private GetOpenIdConfidentialClientRedirectUri subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new GetOpenIdConfidentialClientRedirectUri(mockConfidentialClientRepository);
    }

    @Test
    public void clientFoundRedirectMatchesShouldBeOK() throws URISyntaxException, RecordNotFoundException, InformClientException, InformResourceOwnerException {

        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);

        when(mockConfidentialClientRepository.getByClientId(client.getUuid())).thenReturn(confidentialClient);

        ResponseTypeException rootCause = new ResponseTypeException("");

        boolean actual = subject.run(client.getUuid(), client.getRedirectURI(), rootCause);
        assertThat(actual, is(true));
    }

    @Test
    public void clientNotFoundShouldThrowInformResourceOwnerException() throws RecordNotFoundException, URISyntaxException {
        UUID clientId = UUID.randomUUID();
        URI redirectURI = new URI("https://rootservices.org");
        ResponseTypeException rootCause = new ResponseTypeException("");

        when(mockConfidentialClientRepository.getByClientId(clientId)).thenThrow(RecordNotFoundException.class);

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

        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);

        when(mockConfidentialClientRepository.getByClientId(client.getUuid())).thenReturn(confidentialClient);

        try {
            subject.run(client.getUuid(), redirectURI, rootCause);
            fail("InformResourceOwnerException expected");
        } catch(InformClientException e) {
            fail("InformResourceOwnerException expected");
        } catch(InformResourceOwnerException e) {
            assertThat(e.getDomainCause() instanceof ResponseTypeException, is(true));
            assertThat(e.getCode(), is(ErrorCode.REDIRECT_URI_MISMATCH.getCode()));
        }
    }
}