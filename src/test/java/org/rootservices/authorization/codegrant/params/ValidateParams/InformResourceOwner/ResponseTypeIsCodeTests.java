package org.rootservices.authorization.codegrant.params.validateParams.InformResourceOwner;

import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.exception.resourceowner.MissingClientIdException;
import org.rootservices.authorization.codegrant.params.ValidateParams;
import org.rootservices.authorization.codegrant.params.ValidateParamsImpl;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;

/*
 * Feature: Client id fails validation and response type passes validation.
 */
public class ResponseTypeIsCodeTests {

    private List<String> responseTypes;
    private ValidateParams subject;

    @Before
    public void setUp() {
        subject = new ValidateParamsImpl();
        ResponseType rt = ResponseType.CODE;
        responseTypes = new ArrayList<>();
        responseTypes.add(rt.toString());
    }

    @Test
    public void validate() throws InformResourceOwnerException, InformClientException {
        UUID uuid = UUID.randomUUID();
        List<String> clientIds = new ArrayList<>();
        clientIds.add(uuid.toString());

        boolean isValid = subject.run(clientIds, responseTypes);
        assertThat(isValid).isEqualTo(true);
    }

    @Test(expected = MissingClientIdException.class)
    public void clientIdsIsNull() throws InformResourceOwnerException, InformClientException {
        List<String> clientIds = null;

        subject.run(clientIds, responseTypes);
    }

    @Test(expected = MissingClientIdException.class)
    public void clientIdsIsEmptyList() throws InformResourceOwnerException, InformClientException {
        List<String> clientIds = new ArrayList<>();

        subject.run(clientIds, responseTypes);
    }

    @Test(expected = MissingClientIdException.class)
    public void clientIdsHasEmptyValue() throws InformResourceOwnerException, InformClientException {
        List<String> clientIds = new ArrayList<>();
        clientIds.add("");

        subject.run(clientIds, responseTypes);
    }
}

