package org.rootservices.authorization.codegrant.builder;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.request.AuthRequest;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;

public class AuthRequestBuilderImplTest {

    AuthRequestBuilderImpl subject;

    @Before
    public void setUp() {
        subject = new AuthRequestBuilderImpl();
    }

    @Test
    public void build() throws InformResourceOwnerException {
        String clientId = UUID.randomUUID().toString();
        String responseType = ResponseType.CODE.toString();

        AuthRequest actual = subject.build(clientId, responseType);
        assertThat(clientId).isEqualTo(actual.getClientId().toString());
        assertThat(responseType).isEqualTo(actual.getResponseType().toString());
    }

    @Test
    public void buildUnknownResponseType() throws InformResourceOwnerException {
        String clientId = UUID.randomUUID().toString();
        String responseType = "unknownResponseType";

        AuthRequest actual = subject.build(clientId, responseType);
        assertThat(clientId).isEqualTo(actual.getClientId().toString());
        assertThat(actual.getResponseType()).isNull();
    }

    @Test
    public void buildNullResponseType() throws InformResourceOwnerException {
        String clientId = UUID.randomUUID().toString();
        String responseType = null;

        AuthRequest actual = subject.build(clientId, responseType);
        assertThat(clientId).isEqualTo(actual.getClientId().toString());
        assertThat(actual.getResponseType()).isNull();
    }

    @Test(expected=InformResourceOwnerException.class)
    public void buildInvalidUUID() throws InformResourceOwnerException {
        String clientId = "invalidUUID";
        String responseType = ResponseType.CODE.toString();

        subject.build(clientId, responseType);
    }

    @Test
    public void buildNullUUID() throws InformResourceOwnerException {
        String clientId = null;
        String responseType = ResponseType.CODE.toString();

        AuthRequest actual = subject.build(clientId, responseType);
        assertThat(actual.getClientId()).isNull();
        assertThat(responseType).isEqualTo(actual.getResponseType().toString());
    }

}