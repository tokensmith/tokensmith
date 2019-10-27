package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation;


import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import net.tokensmith.authorization.parse.exception.RequiredException;


import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by tommackenzie on 3/1/15.
 */
public class ClientIdTest extends BaseTest {

    public Map<String, List<String>> makeValidateParamsWithNonce() {
        Map<String, List<String>> p = super.makeParamsWithNonce();
        p.get("client_id").clear();

        return p;
    }

    @Test
    public void clientIdIsNullShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeValidateParamsWithNonce();
        p.put("client_id", null);

        Exception cause = new RequiredException();
        int errorCode = 1;

        runExpectInformResourceOwnerException(p, cause, errorCode);
    }

    @Test
    public void clientIdIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeValidateParamsWithNonce();

        Exception cause = new RequiredException();
        int errorCode = 1;

        runExpectInformResourceOwnerException(p, cause, errorCode);
    }

    @Test
    public void clientIdIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeValidateParamsWithNonce();
        p.get("client_id").add("invalid");

        Exception cause = new RequiredException();
        int errorCode = 1;

        runExpectInformResourceOwnerException(p, cause, errorCode);
    }

    @Test
    public void clientIdsHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeValidateParamsWithNonce();
        p.get("client_id").add(UUID.randomUUID().toString());
        p.get("client_id").add(UUID.randomUUID().toString());

        Exception cause = new RequiredException();
        int errorCode = 1;

        runExpectInformResourceOwnerException(p, cause, errorCode);
    }

    @Test
    public void clientIdIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeValidateParamsWithNonce();
        p.get("client_id").add("");

        Exception cause = new RequiredException();
        int errorCode = 1;

        runExpectInformResourceOwnerException(p, cause, errorCode);
    }
}
