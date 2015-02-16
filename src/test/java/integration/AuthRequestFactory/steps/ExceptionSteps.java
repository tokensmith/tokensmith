package integration.AuthRequestFactory.steps;

import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.rootservices.authorization.codegrant.constant.ErrorCode;
import org.rootservices.authorization.codegrant.exception.BaseInformException;
import org.rootservices.authorization.codegrant.exception.client.InformClientException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 2/15/15.
 */
public class ExceptionSteps {

    protected BaseInformException expectedException;

    @Then("expect e's code to be $code")
    public void expectEsCodeToBe(@Named("code") String code) {
        ErrorCode ec = ErrorCode.valueOf(code);
        assertThat(expectedException.getCode()).isEqualTo(ec.getCode());
    }
}
