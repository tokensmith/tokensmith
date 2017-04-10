package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation;


import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.RedirectUriException;
import org.rootservices.authorization.parse.exception.RequiredException;
import org.rootservices.authorization.persistence.entity.Client;

import java.util.List;
import java.util.Map;


public class RedirectUriTest extends BaseTest {

    public Map<String, List<String>> makeParamsWithNonce(Client c) {
        Map<String, List<String>> p = super.makeParamsWithNonce(c);
        p.get("redirect_uri").clear();

        return p;
    }

    @Test
    public void redirectUrisIsNullShouldThrowInformResourceOwner() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.put("redirect_uri", null);

        Exception cause = new RequiredException();
        int expectedErrorCode = 1;

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void redirectUriIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);

        Exception cause = new RequiredException();
        int expectedErrorCode = 1;

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void redirectUrisIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("redirect_uri").add("");

        Exception cause = new RequiredException();
        int expectedErrorCode = 1;

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void redirectUrisHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("redirect_uri").add(c.getRedirectURI().toString());
        p.get("redirect_uri").add(c.getRedirectURI().toString());

        Exception cause = new RequiredException();
        int expectedErrorCode = 1;

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void redirectUriIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("redirect_uri").add("invalid-uri");

        int expectedErrorCode = 1;

        runExpectInformResourceOwnerExceptionNoCause(p, expectedErrorCode);
    }

    public void redirectUriIsNotHttpsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("redirect_uri").add("http://rootservices.org");

        Exception cause = new RequiredException();
        int expectedErrorCode = 1;

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void redirectUriDoesNotMatchClientShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("redirect_uri").add("https://rootservices.org/continue");

        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerExceptionNoCause(p, expectedErrorCode);
    }
}
