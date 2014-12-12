package org.rootservices.authorization.codegrant.params.validateParams.InformResourceOwner;

import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InvalidClientIdException;
import org.rootservices.authorization.codegrant.exception.resourceowner.MissingClientIdException;
import org.rootservices.authorization.codegrant.params.ValidateParams;
import org.rootservices.authorization.codegrant.params.ValidateParamsImpl;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 12/9/14.
 *
 * Feature: Client id fails validation and response type fails validation.
 */
public class ResponseTypeMissingTests {

    private ValidateParams subject;

    @Before
    public void setUp() {
        subject = new ValidateParamsImpl();
    }

    @Test(expected=MissingClientIdException.class)
    public void ClientIdAndResponseTypeAreMissing() throws InformResourceOwnerException, InformClientException {
        List<String> responseTypes = new ArrayList<>();
        List<String> clientIds = new ArrayList<>();

        subject.run(clientIds, responseTypes);
    }

    @Test(expected=MissingClientIdException.class)
    public void DuplicateClientIdAndResponseTypeIsMissing() throws InformResourceOwnerException, InformClientException {
        List<String> responseTypes = new ArrayList<>();
        List<String> clientIds = new ArrayList<>();

        UUID uuid1 = UUID.randomUUID();
        clientIds.add(uuid1.toString());
        UUID uuid2 = UUID.randomUUID();
        clientIds.add(uuid2.toString());

        subject.run(clientIds, responseTypes);
    }

    @Test(expected= InvalidClientIdException.class)
    public void ClientIdIsNotUUID() throws InformResourceOwnerException, InformClientException {
        List<String> responseTypes = new ArrayList<>();
        List<String> clientIds = new ArrayList<>();
        clientIds.add("InvalidClientId");

        subject.run(clientIds, responseTypes);
    }
}
