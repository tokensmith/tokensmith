package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation.Nonce;

import helper.ValidateParamsWithNonce;
import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.ResponseTypeException;
import org.rootservices.authorization.openId.grant.redirect.token.authorization.request.factory.exception.NonceException;
import org.rootservices.authorization.persistence.entity.Client;


public class ClientFoundTest extends BaseTest {

    @Test
    public void noncesIsNullShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.nonces = null;

        Exception expectedDomainCause = new NonceException();
        int expectedErrorCode = ErrorCode.NONCE_NULL.getCode();
        String expectedDescription = ErrorCode.NONCE_NULL.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void noncesIsEmptyListShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.nonces.clear();

        Exception expectedDomainCause = new NonceException();
        int expectedErrorCode = ErrorCode.NONCE_EMPTY_LIST.getCode();
        String expectedDescription = ErrorCode.NONCE_EMPTY_LIST.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void noncesHasTwoItemsShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.nonces.clear();
        p.nonces.add("some-nonce");
        p.nonces.add("some-nonce");

        Exception expectedDomainCause = new NonceException();
        int expectedErrorCode = ErrorCode.NONCE_MORE_THAN_ONE_ITEM.getCode();
        String expectedDescription = ErrorCode.NONCE_MORE_THAN_ONE_ITEM.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void noncesIsBlankStringShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.nonces.clear();
        p.nonces.add("");

        Exception expectedDomainCause = new NonceException();
        int expectedErrorCode = ErrorCode.NONCE_EMPTY_VALUE.getCode();
        String expectedDescription = ErrorCode.NONCE_EMPTY_VALUE.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }
}
