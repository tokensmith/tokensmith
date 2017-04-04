package integration.authorization.openid.grant.code.request.ValidateOpenIdParams.validation;

import helper.ValidateParamsAttributes;
import integration.authorization.openid.grant.code.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.RedirectUriException;
import org.rootservices.authorization.parse.exception.RequiredException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class RedirectUriTest extends BaseTest {

    public Map<String, List<String>> makeParams(UUID clientId) {
        Map<String, List<String>> p = super.makeParams();

        p.get("client_id").add(clientId.toString());
        p.get("response_type").add("CODE");

        return p;
    }

    @Test
    public void redirectUrisIsNullShouldThrowInformResourceOwner() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.put("redirect_uri", null);

        Exception cause = new RequiredException();

        runExpectInformResourceOwnerException(p, cause);
    }

    @Test
    public void redirectUriIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());

        Exception cause = new RequiredException();

        runExpectInformResourceOwnerException(p, cause);
    }

    @Test
    public void redirectUrisIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("redirect_uri").add("");

        Exception cause = new RequiredException();

        runExpectInformResourceOwnerException(p, cause);
    }

    @Test
    public void redirectUrisHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("redirect_uri").add(c.getRedirectURI().toString());

        Exception cause = new RequiredException();

        runExpectInformResourceOwnerException(p, cause);
    }

    @Test
    public void redirectUriIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("redirect_uri").add("invalid-uri");

        Exception cause = new RequiredException();

        runExpectInformResourceOwnerException(p, cause);
    }

    public void redirectUriIsNotHttpsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("redirect_uri").add("http://rootservices.org");

        Exception cause = new RequiredException();

        runExpectInformResourceOwnerException(p, cause);
    }

    @Test
    public void redirectUriDoesNotMatchClientShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("redirect_uri").add("https://rootservices.org/continue");

        Exception cause = new RequiredException();

        runExpectInformResourceOwnerException(p, cause);
    }
}
