package integration.code.ValidateParams;

import helper.ValidateParamsAttributes;
import org.junit.Test;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.StateException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.entity.AuthRequest;
import org.rootservices.authorization.persistence.entity.Client;

import java.net.URISyntaxException;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

/**
 * Feature: Passes Validation and returns a AuthRequest instance
 *
 * Given a instance of ValidateParams, subject
 * And a public client, c
 * And c's response_type is, CODE
 * And c's id is a randomly generated UUID
 * And c's redirect uri is, https://rootservices.org
 * And c's scopes are, profile
 * And c is inserted into the database
 */
public class OkTest extends BaseTest {

    /**
     * Scenario 1: Required Parameters
     *
     * When subject.run is executed
     * And clientIds has one item that is c's id.
     * And responseTypes has one item, CODE
     * And redirectUris is a empty list
     * And scopes is a empty list
     * And states is a empty list
     *
     * Then a instance of a AuthRequest, actual is returned
     * And actual's clientId is c's id
     * And actual's responseType is "CODE"
     * And actual's redirectUri is not present
     * And actual's scopes is a empty list
     * And actual's state is not present
     *
     * @throws URISyntaxException
     * @throws StateException
     * @throws InformResourceOwnerException
     * @throws InformClientException
     */
    @Test
    public void requiredParamsShouldBeOK() throws URISyntaxException, StateException, InformResourceOwnerException, InformClientException {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add(c.getResponseType().toString());

        AuthRequest actual = subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);

        assertThat(actual.getClientId()).isEqualTo(c.getUuid());
        assertThat(actual.getResponseType()).isEqualTo(c.getResponseType());
        assertThat(actual.getRedirectURI().isPresent()).isFalse();
        assertThat(actual.getScopes()).isEmpty();
        assertThat(actual.getState().isPresent()).isFalse();
    }

    /**
     * * Scenario 2: Required and Optional parameters.
     *
     * When subject.run is executed
     * And clientIds has one item that is c's id.
     * And responseTypes has one item, "CODE"
     * And redirectUris has one item that is c's redirect uri
     * And scopes has one item, 'profile'
     * And states has one item, 'some-state'
     *
     * Then a instance of a AuthRequest, actual is returned
     * And actual's clientId is c's id
     * And actual's responseType is "CODE"
     * And actual's redirectUri is c's redirect uri
     * And actual's scopes has one item 'profile'
     * And actual's state has one item 'some-state'
     *
     * @throws URISyntaxException
     * @throws StateException
     * @throws InformResourceOwnerException
     * @throws InformClientException
     */
    @Test
    public void requiredAndOptParamsShouldBeOK() throws URISyntaxException, StateException, InformResourceOwnerException, InformClientException {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add(c.getResponseType().toString());
        p.redirectUris.add(c.getRedirectURI().toString());
        p.scopes.add("profile");
        p.states.add("some-state");

        AuthRequest actual = subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);

        assertThat(actual.getClientId()).isEqualTo(c.getUuid());
        assertThat(actual.getResponseType()).isEqualTo(c.getResponseType());
        assertThat(actual.getRedirectURI().isPresent()).isTrue();
        assertThat(actual.getRedirectURI().get()).isEqualTo(c.getRedirectURI());
        assertThat(actual.getScopes()).isNotNull();
        assertThat(actual.getScopes().size()).isEqualTo(1);
        assertThat(actual.getScopes().get(0)).isEqualTo("profile");
        assertThat(actual.getState().isPresent()).isTrue();
        assertThat(actual.getState().get()).isEqualTo("some-state");
    }
}
