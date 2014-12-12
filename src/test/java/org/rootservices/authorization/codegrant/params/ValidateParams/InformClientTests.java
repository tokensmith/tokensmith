package org.rootservices.authorization.codegrant.params.validateParams;

import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.client.MissingResponseTypeException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.params.ValidateParams;
import org.rootservices.authorization.codegrant.params.ValidateParamsImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 12/9/14.
 *
 * Feature: Response type fails validation and client id passes validation.
 */
public class InformClientTests {

    private List<String> clientIds;
    private ValidateParams subject;

    @Before
    public void setUp() {
        subject = new ValidateParamsImpl();
        UUID uuid = UUID.randomUUID();
        clientIds = new ArrayList<>();
        clientIds.add(uuid.toString());
    }

    @Test(expected = MissingResponseTypeException.class)
    public void responseTypesIsNull() throws InformResourceOwnerException, InformClientException {
        List<String> responseTypes = null;

        subject.run(clientIds, responseTypes);
    }

    @Test(expected = MissingResponseTypeException.class)
    public void responseTypesIsEmptyList() throws InformResourceOwnerException, InformClientException {
        List<String> responseTypes = new ArrayList<>();

        subject.run(clientIds, responseTypes);
    }

    @Test(expected = MissingResponseTypeException.class)
    public void responseTypesHasEmptyValue() throws InformResourceOwnerException, InformClientException {
        List<String> responseTypes = new ArrayList<>();
        responseTypes.add("");

        subject.run(clientIds, responseTypes);
    }
}
