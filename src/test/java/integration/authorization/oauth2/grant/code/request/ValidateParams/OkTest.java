package integration.authorization.oauth2.grant.code.request.ValidateParams;

import helper.ValidateParamsAttributes;
import org.junit.Test;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.exception.StateException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.entity.AuthRequest;
import org.rootservices.authorization.persistence.entity.Client;

import java.net.URISyntaxException;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;


public class OkTest extends BaseTest {

    @Test
    public void requiredParamsShouldBeOK() throws URISyntaxException, StateException, InformResourceOwnerException, InformClientException {
        Client c = loadConfidentialClient();

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

    @Test
    public void requiredAndOptionalParamsShouldBeOK() throws URISyntaxException, StateException, InformResourceOwnerException, InformClientException {
        Client c = loadConfidentialClient();

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
