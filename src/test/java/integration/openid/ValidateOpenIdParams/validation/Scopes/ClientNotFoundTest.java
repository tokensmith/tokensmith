package integration.openid.ValidateOpenIdParams.validation.Scopes;

import helper.ValidateParamsAttributes;
import integration.openid.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;


public class ClientNotFoundTest extends BaseTest {

    private static String REDIRECT_URI = "https://rootservices.org";

    public ValidateParamsAttributes makeValidateParamsAttributes() {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(UUID.randomUUID().toString());
        p.redirectUris.add(REDIRECT_URI);
        p.responseTypes.add(ResponseType.CODE.toString());

        return p;
    }

    @Test
    public void scopeIsInvalidShouldThrowInformResourceOwnerException() throws Exception {

        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.scopes.add("invalid-scope");

        Exception expectedDomainCause = new RecordNotFoundException();
        int expectedErrorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void scopesHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.scopes.add("profile");
        p.scopes.add("profile");

        Exception expectedDomainCause = new RecordNotFoundException();
        int expectedErrorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void scopeIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.scopes.add("");

        Exception expectedDomainCause = new RecordNotFoundException();
        int expectedErrorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }
}
