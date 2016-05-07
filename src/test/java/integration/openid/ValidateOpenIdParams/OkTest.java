package integration.openid.ValidateOpenIdParams;

import helper.ValidateParamsAttributes;
import org.junit.Test;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.exception.StateException;
import org.rootservices.authorization.openId.grant.code.authorization.request.entity.OpenIdAuthRequest;
import org.rootservices.authorization.persistence.entity.Client;

import java.net.URISyntaxException;


import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * Created by tommackenzie on 3/1/15.
 */
public class OkTest extends BaseTest {

    @Test
    public void requiredParamsShouldBeOK() throws URISyntaxException, StateException, InformResourceOwnerException, InformClientException {
        Client c = loadClientWithOpenIdScope.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.redirectUris.add(c.getRedirectURI().toString());
        p.responseTypes.add(c.getResponseType().toString());

        OpenIdAuthRequest actual = subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);

        assertThat(actual.getClientId(), is(c.getUuid()));
        assertThat(actual.getResponseType(), is(c.getResponseType()));
        assertThat(actual.getRedirectURI(), is(c.getRedirectURI()));
        assertThat(actual.getScopes().size(), is(0));
        assertThat(actual.getState().isPresent(), is(false));
    }

    @Test
    public void requiredAndOptionalParamsShouldBeOK() throws URISyntaxException, StateException, InformResourceOwnerException, InformClientException {
        Client c = loadClientWithOpenIdScope.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add(c.getResponseType().toString());
        p.redirectUris.add(c.getRedirectURI().toString());
        p.scopes.add(c.getScopes().get(0).getName());
        p.states.add("some-state");

        OpenIdAuthRequest actual = subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);

        assertThat(actual.getClientId(), is(c.getUuid()));
        assertThat(actual.getResponseType(), is(c.getResponseType()));
        assertThat(actual.getRedirectURI(), is(c.getRedirectURI()));
        assertThat(actual.getScopes(), not(nullValue()));
        assertThat(actual.getScopes().size(), is(1));
        assertThat(actual.getScopes().get(0), is("openid"));
        assertThat(actual.getState().isPresent(), is(true));
        assertThat(actual.getState().get(), is("some-state"));
    }
}
