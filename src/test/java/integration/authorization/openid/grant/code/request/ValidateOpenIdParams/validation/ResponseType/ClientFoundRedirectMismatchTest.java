package integration.authorization.openid.grant.code.request.ValidateOpenIdParams.validation.ResponseType;

import helper.ValidateParamsAttributes;
import integration.authorization.openid.grant.code.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.ResponseTypeException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.util.UUID;


public class ClientFoundRedirectMismatchTest extends BaseTest {

    public static String REDIRECT_URI = "https://rootservices.org/continue";

    public ValidateParamsAttributes makeValidateParamsAttributes(UUID clientId) {
        ValidateParamsAttributes p = new ValidateParamsAttributes();

        p.clientIds.add(clientId.toString());
        p.redirectUris.add(REDIRECT_URI);

        return p;
    }

    @Test
    public void responseTypeIsNullShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = makeValidateParamsAttributes(c.getId());
        p.responseTypes = null;

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void responseTypeIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = makeValidateParamsAttributes(c.getId());

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);

    }

    @Test
    public void responseTypeIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = makeValidateParamsAttributes(c.getId());
        p.responseTypes.add("invalid-response-type");

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void responseTypeHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = makeValidateParamsAttributes(c.getId());
        p.responseTypes.add("CODE");
        p.responseTypes.add("CODE");

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void responseTypeIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = makeValidateParamsAttributes(c.getId());
        p.responseTypes.add("");

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }
}
