package integration.authorization.openid.grant.token.request.ValidateOpenIdParams;

import helper.ValidateParamsAttributes;
import helper.ValidateParamsWithNonce;
import org.junit.Test;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.entity.OpenIdAuthRequest;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * Created by tommackenzie on 3/1/15.
 */
public class OkTest extends BaseTest {

    @Test
    public void requiredParamsShouldBeOK() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonceRequiredOnly(c);

        OpenIdAuthRequest actual = subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states, p.nonces);

        assertThat(actual.getClientId(), is(c.getUuid()));
        assertThat(actual.getResponseTypes().size(), is(1));
        assertThat(actual.getResponseTypes().get(0), is(c.getResponseTypes().get(0).getName()));
        assertThat(actual.getRedirectURI(), is(c.getRedirectURI()));
        assertThat(actual.getScopes().size(), is(0));
        assertThat(actual.getState().isPresent(), is(false));
    }

    @Test
    public void requiredAndOptionalParamsShouldBeOK() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);

        OpenIdAuthRequest actual = subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states, p.nonces);

        assertThat(actual.getClientId(), is(c.getUuid()));
        assertThat(actual.getResponseTypes().size(), is(1));
        assertThat(actual.getResponseTypes().get(0), is(c.getResponseTypes().get(0).getName()));
        assertThat(actual.getRedirectURI(), is(c.getRedirectURI()));
        assertThat(actual.getScopes(), not(nullValue()));
        assertThat(actual.getScopes().size(), is(1));
        assertThat(actual.getScopes().get(0), is("openid"));
        assertThat(actual.getState().isPresent(), is(true));
        assertThat(actual.getState().get(), is("some-state"));
    }
}
