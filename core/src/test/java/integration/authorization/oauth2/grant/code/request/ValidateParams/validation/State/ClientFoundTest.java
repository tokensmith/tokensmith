package integration.authorization.oauth2.grant.code.request.ValidateParams.validation.State;


import integration.authorization.oauth2.grant.code.request.ValidateParams.BaseTest;
import org.junit.Test;
import net.tokensmith.parser.exception.OptionalException;
import net.tokensmith.repository.entity.Client;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ClientFoundTest extends BaseTest {

    public Map<String, List<String>> makeParams(UUID clientId) {
        Map<String, List<String>> p = super.makeParams();
        p.get("client_id").add(clientId.toString());
        p.get("response_type").add("CODE");

        return p;
    }

    @Test
    public void stateHasTwoItemsShouldThrowInformClientException() throws URISyntaxException {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("state").add("some-state");
        p.get("state").add("some-state");

        Exception cause = new OptionalException();
        String expectedDescription = "state has more than one value";
        String expectedError = "invalid_request";

        runExpectInformClientException(p, cause, 1, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void stateIsBlankStringShouldThrowInformClientException() throws URISyntaxException {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("state").add("");

        Exception cause = new OptionalException();
        String expectedDescription = "state is blank or missing";
        String expectedError = "invalid_request";

        runExpectInformClientException(p, cause, 1, expectedError, expectedDescription, c.getRedirectURI());

    }
}
