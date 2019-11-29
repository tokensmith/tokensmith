package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation.Nonce;

import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.parse.exception.RequiredException;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ResponseType;

import java.util.List;
import java.util.Map;


public class ClientFoundRedirectMismatchTest extends BaseTest {

    public static String REDIRECT_URI = "https://tokensmith.net/continue";

    public Map<String, List<String>> makeParams(Client c) {
        Map<String, List<String>> p = super.makeParams();
        p.get("client_id").add(c.getId().toString());

        for(ResponseType responseType: c.getResponseTypes()) {
            p.get("response_type").add(responseType.getName());
        }

        p.get("redirect_uri").add(REDIRECT_URI);

        return p;
    }

    @Test
    public void noncesIsNullShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParams(c);
        p.put("nonce", null);

        Exception cause = new RequiredException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void noncesIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParams(c);

        Exception cause = new RequiredException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);

    }

    @Test
    public void noncesHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParams(c);
        p.get("nonce").add("some-nonce");
        p.get("nonce").add("some-nonce");

        Exception cause = new RequiredException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void noncesIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParams(c);
        p.get("nonce").add("");

        Exception cause = new RequiredException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }
}
