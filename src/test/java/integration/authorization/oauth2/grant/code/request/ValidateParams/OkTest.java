package integration.authorization.oauth2.grant.code.request.ValidateParams;

import helper.ValidateParamsAttributes;
import org.junit.Test;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.StateException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.entity.AuthRequest;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


public class OkTest extends BaseTest {

    @Test
    public void requiredParamsShouldBeOK() throws URISyntaxException, StateException, InformResourceOwnerException, InformClientException {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getId().toString());

        for(ResponseType rt: c.getResponseTypes()) {
            p.responseTypes.add(rt.getName());
        }

        AuthRequest actual = validateParamsCodeResponseType.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);

        assertThat(actual.getClientId(), is(c.getId()));
        assertThat(actual.getResponseTypes().size(),is(1));
        assertThat(actual.getResponseTypes().get(0), is(c.getResponseTypes().get(0).getName()));
        assertThat(actual.getRedirectURI().isPresent(), is(false));
        assertThat(actual.getScopes().isEmpty(), is(true));
        assertThat(actual.getState().isPresent(), is(false));
    }

    @Test
    public void requiredAndOptionalParamsShouldBeOK() throws URISyntaxException, StateException, InformResourceOwnerException, InformClientException {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getId().toString());

        for(ResponseType rt: c.getResponseTypes()) {
            p.responseTypes.add(rt.getName());
        }

        p.redirectUris.add(c.getRedirectURI().toString());
        p.scopes.add("profile");
        p.states.add("some-state");

        AuthRequest actual = validateParamsCodeResponseType.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);

        assertThat(actual.getClientId(), is(c.getId()));
        assertThat(actual.getResponseTypes().size(), is(1));
        assertThat(actual.getResponseTypes().get(0), is(c.getResponseTypes().get(0).getName()));
        assertThat(actual.getRedirectURI().isPresent(), is(true));
        assertThat(actual.getRedirectURI().get(), is(c.getRedirectURI()));
        assertThat(actual.getScopes(), is(notNullValue()));
        assertThat(actual.getScopes().size(), is(1));
        assertThat(actual.getScopes().get(0), is("profile"));
        assertThat(actual.getState().isPresent(), is(true));
        assertThat(actual.getState().get(), is("some-state"));
    }
}
