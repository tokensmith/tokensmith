package integration.authorization.oauth2.grant.token.request.ValidateParams.validation.ResponseType;

import integration.authorization.oauth2.grant.token.request.ValidateParams.BaseTest;
import org.junit.Test;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ClientNotFoundTest extends BaseTest {

    public Map<String, List<String>> makeParams() {
        Map<String, List<String>> p = super.makeParams();
        p.get("client_id").add(UUID.randomUUID().toString());

        return p;
    }

    @Test
    public void responseTypeIsNullShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParams();
        p.put("response_type", null);

        RecordNotFoundException cause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, cause, errorCode);
    }

    @Test
    public void responseTypeIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParams();

        RecordNotFoundException cause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, cause, errorCode);
    }

    @Test
    public void responseTypeIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParams();
        p.get("response_type").add("invalid-response-type");

        RecordNotFoundException cause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, cause, errorCode);
    }

    @Test
    public void responseTypeHasTwoItemsShouldThrowInformResourceException() throws Exception {
        Map<String, List<String>> p = makeParams();
        p.get("response_type").add("TOKEN");
        p.get("response_type").add("TOKEN");

        RecordNotFoundException cause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, cause, errorCode);
    }

    @Test
    public void responseTypeIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParams();
        p.get("response_type").add("");

        RecordNotFoundException cause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, cause, errorCode);
    }
}
