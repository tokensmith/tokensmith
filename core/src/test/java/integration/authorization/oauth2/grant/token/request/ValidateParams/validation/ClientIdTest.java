package integration.authorization.oauth2.grant.token.request.ValidateParams.validation;


import integration.authorization.oauth2.grant.token.request.ValidateParams.BaseTest;
import net.tokensmith.parser.exception.RequiredException;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by tommackenzie on 3/1/15.
 */
public class ClientIdTest extends BaseTest {

    @Test
    public void clientIdIsNullShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParams();
        p.put("client_id", null);

        Exception cause = new RequiredException();

        runExpectInformResourceOwnerException(p, cause, 1);
    }

    @Test
    public void clientIdIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParams();

        Exception cause = new RequiredException();

        runExpectInformResourceOwnerException(p, cause, 1);
    }

    @Test
    public void clientIdIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParams();
        p.get("client_id").add("invalid");

        Exception cause = new RequiredException();

        runExpectInformResourceOwnerException(p, cause, 1);
    }

    @Test
    public void clientIdsHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParams();
        p.get("client_id").add(UUID.randomUUID().toString());
        p.get("client_id").add(UUID.randomUUID().toString());

        Exception cause = new RequiredException();

        runExpectInformResourceOwnerException(p, cause, 1);
    }

    @Test
    public void clientIdIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParams();
        p.get("client_id").add("");

        Exception cause = new RequiredException();

        runExpectInformResourceOwnerException(p, cause, 1);
    }
}
