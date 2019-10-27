package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation.Nonce;

import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.List;
import java.util.Map;


public class ClientNotFoundTest extends BaseTest {

    @Test
    public void noncesIsNullShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParamsWithNonce();
        p.put("nonce", null);

        Exception cause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, cause, errorCode);
    }

    @Test
    public void noncesIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParamsWithNonce();
        p.get("nonce").clear();

        Exception cause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, cause, errorCode);
    }

    @Test
    public void noncesHasTwoItemsShouldThrowInformResourceException() throws Exception {
        Map<String, List<String>> p = makeParamsWithNonce();
        p.get("nonce").clear();
        p.get("nonce").add("some-nonce");
        p.get("nonce").add("some-nonce");

        Exception cause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, cause, errorCode);
    }

    @Test
    public void noncesIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParamsWithNonce();
        p.get("nonce").clear();
        p.get("nonce").add("");

        Exception cause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, cause, errorCode);
    }
}
