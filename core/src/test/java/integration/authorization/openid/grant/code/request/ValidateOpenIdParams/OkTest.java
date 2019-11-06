package integration.authorization.openid.grant.code.request.ValidateOpenIdParams;

import org.junit.Test;
import net.tokensmith.authorization.openId.grant.redirect.code.authorization.request.entity.OpenIdAuthRequest;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ResponseType;


import java.util.List;
import java.util.Map;

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
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams();
        p.get("client_id").add(c.getId().toString());
        p.get("redirect_uri").add(c.getRedirectURI().toString());

        for(ResponseType responseType: c.getResponseTypes()) {
            p.get("response_type").add(responseType.getName());
        }

        OpenIdAuthRequest actual = subject.run(p);

        assertThat(actual.getClientId(), is(c.getId()));
        assertThat(actual.getResponseTypes().size(), is(1));
        assertThat(actual.getResponseTypes().get(0), is(c.getResponseTypes().get(0).getName()));
        assertThat(actual.getRedirectURI(), is(c.getRedirectURI()));
        assertThat(actual.getScopes().size(), is(0));
        assertThat(actual.getState().isPresent(), is(false));
    }

    @Test
    public void requiredAndOptionalParamsShouldBeOK() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams();
        p.get("client_id").add(c.getId().toString());
        p.get("redirect_uri").add(c.getRedirectURI().toString());

        for(ResponseType responseType: c.getResponseTypes()) {
            p.get("response_type").add(responseType.getName());
        }

        p.get("scope").add(c.getScopes().get(0).getName());
        p.get("state").add("some-state");

        OpenIdAuthRequest actual = subject.run(p);

        assertThat(actual.getClientId(), is(c.getId()));
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
