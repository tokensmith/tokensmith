package integration.AuthRequestFactory.steps.responseType;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.factory.AuthRequestFactory;
import org.rootservices.authorization.codegrant.request.AuthRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 2/11/15.
 */
public class InformResourceOwnerSteps extends CommonSteps {

    private AuthRequestFactory authRequestFactory;

    private UUID inputClientId;

    private List<String> clientIds;
    private AuthRequest authRequest;

    public InformResourceOwnerSteps(AuthRequestFactory authRequestFactory) {
        this.authRequestFactory = authRequestFactory;
    }

    @Given("the parameter client ids has one item assigned to a randomly generated UUID")
    public void setRandomClientId() {
        inputClientId = UUID.randomUUID();
        clientIds = new ArrayList<>();
        clientIds.add(inputClientId.toString());
    }

    @When("a AuthRequest is created$")
    public void makeAuthRequest() {
        try {
            authRequest = authRequestFactory.makeAuthRequest(clientIds, responseTypes, null, null);
            fail("InformResourceOwnerException was expected to be thrown.");
        } catch (InformClientException e) {
            fail(e.getClass().toString() + " was raised and caused by, " + e.getDomainCause());
        } catch (InformResourceOwnerException e) {
            expectedException = e;
        }
    }

    @Then("expect a InformResourceOwnerException to be thrown, e")
    public void expectExceptionInstanceOfInformClientException() {
        assertThat(expectedException instanceof InformResourceOwnerException).isTrue();
    }
}
