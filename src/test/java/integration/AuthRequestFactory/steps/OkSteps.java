package integration.AuthRequestFactory.steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.rootservices.authorization.codegrant.exception.InformClientException;
import org.rootservices.authorization.codegrant.exception.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.factory.AuthRequestFactory;
import org.rootservices.authorization.codegrant.request.AuthRequest;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.entity.Scope;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 2/4/15.
 */
public class OkSteps {

    private AuthRequestFactory authRequestFactory;

    private UUID inputClientClientId;
    private Optional<URI> inputRedirectURI;
    private List<Scope> inputScopes;

    private List<String> clientIds;
    private List<String> responseTypes;
    private List<String> redirectURIs;
    private List<String> scopes;
    private AuthRequest authRequest;

    public OkSteps(AuthRequestFactory authRequestFactory) {
        this.authRequestFactory = authRequestFactory;
    }

    @Given("the parameter client ids has one item assigned to a randomly generated UUID")
    public void setRandomClientId() {
        inputClientClientId = UUID.randomUUID();
        clientIds = new ArrayList<>();
        clientIds.add(inputClientClientId.toString());
    }

    @Given("the parameter response types has one item assigned to CODE")
    public void setResponseTypeToCode() {
        responseTypes = new ArrayList<>();
        responseTypes.add("code");
    }

    @Given("the redirect uri is https://rootservices.org")
    public void setRedirectUri() throws URISyntaxException {
        inputRedirectURI = Optional.ofNullable(new URI("https://rootservices.org"));
        redirectURIs = new ArrayList<>();
        redirectURIs.add(inputRedirectURI.get().toString());
    }

    @Given("the scopes is a list with the first item’s value assigned to PROFILE")
    public void setScopes() throws Throwable {
        inputScopes = new ArrayList<>();
        inputScopes.add(Scope.PROFILE);

        scopes = new ArrayList<>();
        scopes.add(Scope.PROFILE.toString());
    }

    @When("a AuthRequest is created$")
    public void makeAuthRequest() {
        try {
            authRequest = authRequestFactory.makeAuthRequest(clientIds, responseTypes, redirectURIs, scopes);
        } catch (InformResourceOwnerException|InformClientException e) {
            fail(e.getClass().toString() + " was raised and caused by, " + e.getDomainCause());
        }
    }

    @Then("the client id is equal to the input value$")
    public void compareClientIds() {
        assertThat(authRequest.getClientId()).isEqualTo(inputClientClientId);
    }

    @Then("the response type is CODE")
    public void responseTypeIsCode() {
        assertThat(authRequest.getResponseType()).isEqualTo(ResponseType.CODE);
    }

    @Then("the redirect uri is a empty optional")
    public void redirectUriIsEmptyOptional() {
        assertThat(authRequest.getRedirectURI()).isEqualTo(Optional.empty());
    }

    @Then("the scopes are null$")
    public void scopesAreNull() {
        assertThat(authRequest.getScopes()).isEqualTo(null);
    }

    @Then("the redirect uri is https://rootservices.org")
    public void compareRedirectUri() throws Throwable {
        assertThat(authRequest.getRedirectURI()).isEqualTo(inputRedirectURI);
    }

    @Then("the scopes is a list with the first item’s value assigned to scope$")
    public void compareScopes() throws Throwable {
        assertThat(authRequest.getScopes()).isEqualTo(inputScopes);
    }
}