package integration.AuthRequestFactory.steps.ScopesValidation.InformResourceOwner;

import integration.AuthRequestFactory.steps.ScopesValidation.CommonSteps;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.rootservices.authorization.codegrant.exception.InformClientException;
import org.rootservices.authorization.codegrant.exception.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.factory.AuthRequestFactory;
import org.rootservices.authorization.codegrant.factory.exception.ResponseTypeException;
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
 * Created by tommackenzie on 2/21/15.
 */
public class RedirectMismatchSteps extends CommonSteps{

    private AuthRequestFactory authRequestFactory;
    private ClientRepository clientRepository;

    private Client client;
    private AuthRequest authRequest;

    private List<String> clientIds;
    private List<String> redirectUris;

    public RedirectMismatchSteps(AuthRequestFactory authRequestFactory, ClientRepository clientRepository) {
        this.authRequestFactory = authRequestFactory;
        this.clientRepository = clientRepository;
    }

    @Given("a client, c")
    public void aClientC() throws URISyntaxException {

        UUID clientId = UUID.randomUUID();
        ResponseType rt = ResponseType.CODE;
        URI redirectURI = new URI("https://rootservices.org");

        client = new Client(clientId, rt, redirectURI);
    }

    @Given("c's redirect uri is assigned to $uri")
    public void redirectUriIsAssignedTo(@Named("uri") String uri)  throws URISyntaxException {
        URI redirectURI = new URI("https://rootservices.org");
        client.setRedirectURI(redirectURI);
    }

    @Given("c is persisted to the database")
    public void cIsPersistedToTheDatabase() {
        clientRepository.insert(client);
    }

    @Given("c's redirect uri is $uri")
    public void cSRedirectUriIs(@Named("uri") String uri) throws URISyntaxException {
        URI redirectUri = new URI(uri);
        client.setRedirectURI(redirectUri);
    }

    @Given("the parameter client ids has one item assigned to c's UUID")
    public void setClientIdsAndInsertClient() throws URISyntaxException {
        clientIds = new ArrayList<>();
        clientIds.add(client.getUuid().toString());
    }

    @Given("the parameter redirect uris has one item assigned to $uri")
    public void theParameterRedirectUrisHasOneItemsAssignedTo(@Named("uri") String uri) {
        this.redirectUris = new ArrayList<>();
        this.redirectUris.add(uri);
    }

    @When("a AuthRequest is created$")
    public void makeAuthRequest() {
        try {
            authRequest = authRequestFactory.makeAuthRequest(clientIds, null, redirectUris, scopes);
            fail("InformResourceOwnerException was expected to be thrown.");
        } catch (InformClientException e) {
            fail(e.getClass().toString() + " was raised and caused by, " + e.getDomainCause());
        } catch (InformResourceOwnerException e) {
            expectedException = e;
        }
    }

    @Then("expect a InformResourceOwnerException to be thrown, e")
    public void thenExpectAInformResourceOwnerExceptionToBeThrownE() {
        assertThat(expectedException instanceof InformResourceOwnerException).isTrue();
    }

    @Then("expect e's cause to be a ResponseTypeException")
    public void thenExpectEsCauseToBeAResponseTypeException() {
        assertThat(expectedException.getDomainCause() instanceof ResponseTypeException).isTrue();
    }
}
