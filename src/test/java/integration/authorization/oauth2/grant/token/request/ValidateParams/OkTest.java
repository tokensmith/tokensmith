package integration.authorization.oauth2.grant.token.request.ValidateParams;

import helper.ValidateParamsAttributes;
import org.junit.Test;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.StateException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.entity.AuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.persistence.entity.Client;

import java.net.URISyntaxException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 5/21/16.
 */
public class OkTest extends BaseTest {

    @Test
    public void requiredParamsShouldBeOK() throws URISyntaxException, StateException, InformResourceOwnerException, InformClientException {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getId().toString());
        p.responseTypes.add(c.getResponseTypes().get(0).getName());

        AuthRequest actual = validateParamsTokenResponseType.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);

        assertThat(actual.getClientId()).isEqualTo(c.getId());
        assertThat(actual.getResponseTypes().size()).isEqualTo(1);
        assertThat(actual.getResponseTypes().get(0)).isEqualTo(c.getResponseTypes().get(0).getName());
        assertThat(actual.getRedirectURI().isPresent()).isFalse();
        assertThat(actual.getScopes()).isEmpty();
        assertThat(actual.getState().isPresent()).isFalse();
    }

    @Test
    public void requiredAndOptionalParamsShouldBeOK() throws URISyntaxException, StateException, InformResourceOwnerException, InformClientException {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getId().toString());
        p.responseTypes.add(c.getResponseTypes().get(0).getName());
        p.redirectUris.add(c.getRedirectURI().toString());
        p.scopes.add("profile");
        p.states.add("some-state");

        AuthRequest actual = validateParamsTokenResponseType.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);

        assertThat(actual.getClientId()).isEqualTo(c.getId());
        assertThat(actual.getResponseTypes().size()).isEqualTo(1);
        assertThat(actual.getResponseTypes().get(0)).isEqualTo(c.getResponseTypes().get(0).getName());
        assertThat(actual.getRedirectURI().isPresent()).isTrue();
        assertThat(actual.getRedirectURI().get()).isEqualTo(c.getRedirectURI());
        assertThat(actual.getScopes()).isNotNull();
        assertThat(actual.getScopes().size()).isEqualTo(1);
        assertThat(actual.getScopes().get(0)).isEqualTo("profile");
        assertThat(actual.getState().isPresent()).isTrue();
        assertThat(actual.getState().get()).isEqualTo("some-state");
    }
}
