package integration.openid.ValidateOpenIdParams.validation.State;

import helper.ValidateParamsAttributes;
import integration.openid.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.StateException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.net.URISyntaxException;
import java.util.UUID;

/**
 * /**
 * Scenario: States fails validation And Client is found And redirects don't match.
 *
 * Given a client, c, exists in the db
 * And c's redirect uri is https://rootservices.org
 * And client ids has one item that is assigned to c's UUID
 * And redirect uris has one item that is assigned to https://rootservices.org/continue
 * And response types has one item that is CODE
 * And states is [method]
 * When the params are validated
 * Then raise a InformResourceOwner exception, e
 * And expect e's cause to be [expectedDomainCause]
 * And expects e's error code to be [errorCode]
 */
public class RedirectMismatchTest extends BaseTest {

    public ValidateParamsAttributes makeValidateParamsAttributes(UUID uuid) {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(uuid.toString());
        p.responseTypes.add(ResponseType.CODE.toString());
        p.redirectUris.add("https://rootservices.org/continue");

        return p;
    }

    @Test
    public void duplicate() throws URISyntaxException {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = makeValidateParamsAttributes(c.getUuid());
        p.states.add("some-state");
        p.states.add("some-state");

        Exception expectedDomainCause = new StateException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void emptyValue() throws URISyntaxException {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = makeValidateParamsAttributes(c.getUuid());
        p.states.add("");

        Exception expectedDomainCause = new StateException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }
}