package integration.authorization.oauth2.grant.token.request.ValidateParams.validation;

import helper.ValidateParamsAttributes;
import integration.authorization.oauth2.grant.token.request.ValidateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.RedirectUriException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.StateException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.net.URISyntaxException;


public class RedirectUriTest extends BaseTest {

    @Test
    public void redirectUriIsBlankStringShouldThrowInformResourceOwnerException() throws URISyntaxException, StateException {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add("CODE");
        p.redirectUris.add("");

        Exception expectedDomainCause = new RedirectUriException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_EMPTY_VALUE.getCode();

        runExpectInformResourceOwnerException(p,expectedDomainCause,expectedErrorCode);
    }

    @Test
    public void redirectUrisHasTwoItemsShouldThrowInformResourceOwnerException() throws URISyntaxException, StateException {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add("CODE");
        p.redirectUris.add(c.getRedirectURI().toString());
        p.redirectUris.add(c.getRedirectURI().toString());

        Exception expectedDomainCause = new RedirectUriException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MORE_THAN_ONE_ITEM.getCode();

        runExpectInformResourceOwnerException(p,expectedDomainCause,expectedErrorCode);
    }

    @Test
    public void redirectUriIsInvalidShouldThrowInformResourceOwnerException() throws URISyntaxException, StateException {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add("CODE");
        p.redirectUris.add("invalid-uri");

        Exception expectedDomainCause = new RedirectUriException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_DATA_TYPE.getCode();

        runExpectInformResourceOwnerException(p,expectedDomainCause,expectedErrorCode);
    }

    public void redirectUriIsNotHttpsShouldThrowInformResourceOwnerException() throws URISyntaxException, StateException {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add("CODE");
        p.redirectUris.add("http://rootservices.org");

        Exception expectedDomainCause = new RedirectUriException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_DATA_TYPE.getCode();

        runExpectInformResourceOwnerException(p,expectedDomainCause,expectedErrorCode);
    }

    @Test
    public void redirectUriDoesNotMatchClientShouldThrowInformResourceOwnerException() throws URISyntaxException, StateException {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add("CODE");
        p.redirectUris.add("https://rootservices.org/continue");

        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerExceptionNoCause(p, expectedErrorCode);
    }
}
