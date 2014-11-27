package org.rootservices.authorization.codegrant.params;

import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.client.MissingResponseTypeException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.exception.resourceowner.MissingClientIdException;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;

public class ValidateParamsImplTest {

    private ValidateParams subject;

    @Before
    public void setUp() {
        subject = new ValidateParamsImpl();
    }

    @Test
    public void validate() throws InformResourceOwnerException, InformClientException {
        UUID uuid = UUID.randomUUID();
        ResponseType rt = ResponseType.CODE;

        List<String> clientIds = new ArrayList<>();
        List<String> responseTypes = new ArrayList<>();

        clientIds.add(uuid.toString());
        responseTypes.add(rt.toString());

        boolean isValid = subject.run(clientIds, responseTypes);
        assertThat(isValid).isEqualTo(true);
    }

    @Test(expected = MissingClientIdException.class)
    public void clientIdsIsNull() throws InformResourceOwnerException, InformClientException {
        ResponseType rt = ResponseType.CODE;

        List<String> clientIds = null;
        List<String> responseTypes = new ArrayList<>();

        responseTypes.add(rt.toString());

        subject.run(clientIds, responseTypes);
    }

    @Test(expected = MissingClientIdException.class)
    public void clientIdsIsEmptyList() throws InformResourceOwnerException, InformClientException {
        ResponseType rt = ResponseType.CODE;

        List<String> clientIds = new ArrayList<>();
        List<String> responseTypes = new ArrayList<>();

        responseTypes.add(rt.toString());

        subject.run(clientIds, responseTypes);
    }

    @Test(expected = MissingClientIdException.class)
    public void clientIdsHasEmptyValue() throws InformResourceOwnerException, InformClientException {
        ResponseType rt = ResponseType.CODE;

        List<String> clientIds = new ArrayList<>();
        List<String> responseTypes = new ArrayList<>();

        clientIds.add("");
        responseTypes.add(rt.toString());

        subject.run(clientIds, responseTypes);
    }

    @Test(expected = MissingResponseTypeException.class)
    public void responseTypesIsNull() throws InformResourceOwnerException, InformClientException {
        UUID uuid = UUID.randomUUID();

        List<String> clientIds = new ArrayList<>();
        List<String> responseTypes = null;

        clientIds.add(uuid.toString());

        subject.run(clientIds, responseTypes);
    }

    @Test(expected = MissingResponseTypeException.class)
    public void responseTypesIsEmptyList() throws InformResourceOwnerException, InformClientException {
        UUID uuid = UUID.randomUUID();

        List<String> clientIds = new ArrayList<>();
        List<String> responseTypes = new ArrayList<>();

        clientIds.add(uuid.toString());

        subject.run(clientIds, responseTypes);
    }

    @Test(expected = MissingResponseTypeException.class)
    public void responseTypesHasEmptyValue() throws InformResourceOwnerException, InformClientException {
        UUID uuid = UUID.randomUUID();

        List<String> clientIds = new ArrayList<>();
        List<String> responseTypes = new ArrayList<>();

        clientIds.add(uuid.toString());
        responseTypes.add("");

        subject.run(clientIds, responseTypes);
    }
}

