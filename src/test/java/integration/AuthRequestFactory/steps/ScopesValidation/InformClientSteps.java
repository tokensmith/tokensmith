package integration.AuthRequestFactory.steps.ScopesValidation;

import integration.AuthRequestFactory.steps.ExceptionSteps;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.rootservices.authorization.codegrant.constant.ErrorCode;
import org.rootservices.authorization.codegrant.exception.BaseInformException;
import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.factory.AuthRequestFactory;
import org.rootservices.authorization.codegrant.factory.exception.ScopesException;
import org.rootservices.authorization.codegrant.request.AuthRequest;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.repository.ClientRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 2/13/15.
 */
public class InformClientSteps extends CommonSteps {

    private AuthRequestFactory authRequestFactory;
    private ClientRepository clientRepository;

    private Client client;
    private AuthRequest authRequest;

    private List<String> clientIds;
    private List<String> responseTypes;

    public InformClientSteps(AuthRequestFactory authRequestFactory, ClientRepository clientRepository) {
        this.authRequestFactory = authRequestFactory;
        this.clientRepository = clientRepository;
    }

    @Given("a client exists in the database, c")
    public void insertClient() throws URISyntaxException {
        UUID clientId = UUID.randomUUID();

        ResponseType rt = ResponseType.CODE;
        URI redirectURI = new URI("https://rootservices.org");
        client = new Client(clientId, rt, redirectURI);
        clientRepository.insert(client);
    }

    @Given("the parameter client ids has one item assigned to c's UUID")
    public void setClientIdsAndInsertClient() throws URISyntaxException {
        clientIds = new ArrayList<>();
        clientIds.add(client.getUuid().toString());
    }

    @Given("the parameter response types has one item assigned to CODE")
    public void theParameterResponseTypesHasOneItemAssignedToCode() {
        this.responseTypes = new ArrayList<>();
        this.responseTypes.add("CODE");
    }

    @When("a AuthRequest is created$")
    public void makeAuthRequest() {
        try {
            authRequest = authRequestFactory.makeAuthRequest(clientIds, responseTypes, null, scopes);
            fail("InformClientException was expected to be thrown.");
        } catch (InformClientException e) {
            expectedException = e;
        } catch (InformResourceOwnerException e) {
            fail(e.getClass().toString() + " was raised and caused by, " + e.getDomainCause());
        }
    }

    @Then("expect a InformClientException to be thrown, e")
    public void expectExceptionInstanceOfInformClientException() {
        assertThat(expectedException instanceof InformClientException).isTrue();
    }

    @Then("expect e's error to be $error")
    public void expectErrorToBeInvalidRequest(@Named("error") String error) {
        assertThat(((InformClientException) expectedException).getError()).isEqualTo(error);
    }

    @Then("expect e's redirect uri to be equal to c's redirect uri")
    public void expectRedirectUriToBeClients() {
        assertThat(((InformClientException) expectedException).getRedirectURI()).isEqualTo(client.getRedirectURI());
    }

    @Then("expect e's cause to be a ScopesException")
    public void expectCauseToBeScopesException() {
        assertThat(expectedException.getDomainCause() instanceof ScopesException);
    }
}