package integration.steps;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.factory.AuthRequestFactory;
import org.rootservices.authorization.codegrant.request.AuthRequest;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 2/4/15.
 */
public class AuthRequestFactorySteps {

    @Autowired
    private AuthRequestFactory authRequestFactory;

    private UUID inputClientClientId;
    private List<String> clientIds;
    private List<String> responseTypes;
    private List<String> redirectURIs;
    private List<String> scopes;
    private AuthRequest authRequest;

    @Given("a randomly generated client_id")
    public void a_randomly_generated_client_id() {
        inputClientClientId = UUID.randomUUID();
        clientIds = new ArrayList<>();
        clientIds.add(inputClientClientId.toString());
    }

    @Given("^the response type is code$")
    public void given_the_response_type_is_code() {
        responseTypes = new ArrayList<>();
        responseTypes.add("code");
    }

    @When("^a AuthRequest is created$")
    public void a_authrequest_is_created() {
        try {
            authRequest = authRequestFactory.makeAuthRequest(clientIds, responseTypes, redirectURIs, scopes);
        } catch (InformResourceOwnerException | InformClientException e) {
            fail(e.getClass().toString() + " should not have been raised!");
        }
    }

    @Then("^the client id is equal to the input value$")
    public void the_client_id_is_equal_to_the_input_value() {
        assertThat(authRequest.getClientId()).isEqualTo(inputClientClientId);
    }

    @Then("^the response type is CODE")
    public void then_the_response_type_is_code() {
        assertThat(authRequest.getResponseType()).isEqualTo(ResponseType.CODE);
    }

    @Then("^the redirect uri is null$")
    public void the_redirect_uri_is_null() {
        assertThat(authRequest.getRedirectURI()).isEqualTo(null);
    }

    @Then("^the scopes are null$")
    public void the_scopes_are_null() {
        assertThat(authRequest.getScopes()).isEqualTo(null);
    }

    @Then("^the redirect uri is https://rootservices\\.org$")
    public void the_redirect_uri_is_https_rootservices_org() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^the scopes is a list with the first item’s value assigned to scope$")
    public void the_scopes_is_a_list_with_the_first_item_s_value_assigned_to_scope() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}