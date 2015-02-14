package integration.AuthRequestFactory.steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.rootservices.authorization.codegrant.exception.BaseInformException;
import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.factory.AuthRequestFactory;
import org.rootservices.authorization.codegrant.factory.exception.RedirectUriException;
import org.rootservices.authorization.codegrant.factory.exception.ResponseTypeException;
import org.rootservices.authorization.codegrant.request.AuthRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 2/13/15.
 */
public class RedirectUriValidationSteps {

    private AuthRequestFactory authRequestFactory;

    private AuthRequest authRequest;
    private BaseInformException expectedException;

    private List<String> clientIds;
    private List<String> responseTypes;
    private List<String> redirectUris;

    public RedirectUriValidationSteps(AuthRequestFactory authRequestFactory) {
        this.authRequestFactory = authRequestFactory;
    }

    @Given("the parameter redirect uris has two items assigned to $uri")
    public void theParameterRedirectUrisHasTwoItemsAssignedTo(@Named("uri") String uri) {
        this.redirectUris = new ArrayList<>();
        this.redirectUris.add(uri);
        this.redirectUris.add(uri);
    }

    @Given("the parameter redirect uris has one item assigned to $uri")
    public void theParameterRedirectUrisHasOneItemsAssignedTo(@Named("uri") String uri) {
        this.redirectUris = new ArrayList<>();
        this.redirectUris.add(uri);
    }

    @Given("the parameter client ids has one item assigned to a randomly generated UUID")
    public void setRandomClientId() {
        clientIds = new ArrayList<>();
        clientIds.add(UUID.randomUUID().toString());
    }

    @Given("the parameter response types has one item assigned to CODE")
    public void setResponseTypeToCode() {
        responseTypes = new ArrayList<>();
        responseTypes.add("code");
    }

    @When("a AuthRequest is created$")
    public void makeAuthRequest() {
        try {
            authRequest = authRequestFactory.makeAuthRequest(clientIds, responseTypes, redirectUris, null);
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

    @Then("expect e's cause to be a RedirectUriException")
    public void expectCauseToBeResponseTypeException() {
        assertThat(expectedException.getDomainCause() instanceof RedirectUriException).isTrue();
    }
}
