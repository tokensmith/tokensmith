package org.rootservices.authorization.codegrant.request;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.client.MissingResponseTypeException;
import org.rootservices.authorization.codegrant.exception.client.ResponseTypeIsNotCodeException;
import org.rootservices.authorization.codegrant.exception.resourceowner.ClientNotFoundException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.exception.resourceowner.MissingClientIdException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;

import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidateAuthRequestImplTest {

    @Mock
    private ClientRepository mockClientRepository;

    private ValidateAuthRequest subject;

    @Before
    public void setUp() {
        subject = new ValidateAuthRequestImpl(mockClientRepository);
    }

    @Test
    public void run() throws InformResourceOwnerException, InformClientException, RecordNotFoundException {
        UUID uuid = UUID.randomUUID();

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(uuid);
        authRequest.setResponseType(ResponseType.CODE);

        Client expectedClient = new Client();
        expectedClient.setUuid(uuid);

        when(mockClientRepository.getByUUID(authRequest.getClientId())).thenReturn(expectedClient);

        boolean isValid = subject.run(authRequest);
        assertThat(true).isEqualTo(isValid);
    }

    @Test
    public void runClientNotFound() throws InformResourceOwnerException, InformClientException, RecordNotFoundException {
        UUID uuid = UUID.randomUUID();

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(uuid);
        authRequest.setResponseType(ResponseType.CODE);

        when(mockClientRepository.getByUUID(authRequest.getClientId())).thenThrow(
                RecordNotFoundException.class
        );

        try {
            subject.run(authRequest);
            fail("Expected ClientNotFoundException");
        } catch(ClientNotFoundException e) {
            assertThat(true).isEqualTo(e.getThrowable() instanceof RecordNotFoundException);
        }
    }

    @Test(expected=MissingClientIdException.class)
    public void runClientIdMissing() throws InformResourceOwnerException, InformClientException, RecordNotFoundException {

        AuthRequest authRequest = new AuthRequest();

        subject.run(authRequest);
    }

    @Test(expected=MissingResponseTypeException.class)
    public void runResponseTypeMissing() throws InformResourceOwnerException, InformClientException, RecordNotFoundException {

        UUID uuid = UUID.randomUUID();

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(uuid);

        subject.run(authRequest);
    }

    @Test(expected= ResponseTypeIsNotCodeException.class)
    public void runResponseTypeUnsupported() throws InformResourceOwnerException, InformClientException, RecordNotFoundException {

        UUID uuid = UUID.randomUUID();

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(uuid);
        authRequest.setResponseType(ResponseType.TOKEN);

        subject.run(authRequest);
    }
}