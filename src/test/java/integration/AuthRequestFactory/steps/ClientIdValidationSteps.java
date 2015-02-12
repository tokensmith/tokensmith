package integration.AuthRequestFactory.steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.factory.AuthRequestFactory;
import org.rootservices.authorization.codegrant.factory.exception.ClientIdException;
import org.rootservices.authorization.codegrant.request.AuthRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 2/8/15.
 */
public class ClientIdValidationSteps {

    private AuthRequestFactory authRequestFactory;

    private List<String> clientIds;
    private List<String> responseTypes;
    private AuthRequest authRequest;
    private InformResourceOwnerException expectedException;

    public ClientIdValidationSteps(AuthRequestFactory authRequestFactory) {
        this.authRequestFactory = authRequestFactory;
    }

    @Given("the parameter client ids is assigned null")
    public void setClientIdsNull() {
        clientIds = null;
    }

    @Given("the parameter client ids has no items")
    public void setClientIdsEmptyList() {
        clientIds = new ArrayList<>();
    }

    @Given("the parameter response types has one item assigned to CODE")
    public void setResponseTypes() {
        responseTypes = new ArrayList<>();
        responseTypes.add("CODE");
    }

    @Given("the parameter client ids has one item and it's not a UUID")
    public void setClientIdsNotUUID() {
        clientIds = new ArrayList<>();
        clientIds.add("invalid");
    }

    @Given("the parameter client ids has two randomly generated UUIDs")
    public void setClientIdsTwoUUIDS() {
        UUID clientId = UUID.randomUUID();
        clientIds = new ArrayList<>();
        clientIds.add(clientId.toString());
        clientIds.add(clientId.toString());
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

    @Then("expect a InformResourceOwnerException to be thrown")
    public void exceptionInstanceOfInformResourceOwner() {
        assertThat(expectedException instanceof InformResourceOwnerException).isTrue();
    }

    @Then("expect the cause to be a ClientIdException")
    public void expectTheCauseToBe() {
        assertThat(expectedException.getDomainCause() instanceof ClientIdException).isTrue();
    }

}
