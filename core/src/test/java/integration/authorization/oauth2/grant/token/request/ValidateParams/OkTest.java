package integration.authorization.oauth2.grant.token.request.ValidateParams;

import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.entity.AuthRequest;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ResponseType;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by tommackenzie on 5/21/16.
 */
public class OkTest extends BaseTest {

    public Map<String, List<String>> makeParams(Client c) {
        Map<String, List<String>> p = super.makeParams();
        p.get("client_id").add(c.getId().toString());

        for(ResponseType rt: c.getResponseTypes()) {
            p.get("response_type").add(rt.getName());
        }
        return p;
    }

    @Test
    public void requiredParamsShouldBeOK() throws Exception {
        Client c = loadClient();

        Map<String, List<String>>  p = makeParams(c);

        AuthRequest actual = validateImplicitGrant.run(p);

        assertThat(actual.getClientId(), is(c.getId()));
        assertThat(actual.getResponseTypes().size(), is(1));
        assertThat(actual.getResponseTypes().get(0), is(c.getResponseTypes().get(0).getName()));
        assertThat(actual.getRedirectURI().isPresent(), is(false));
        assertThat(actual.getScopes().isEmpty(), is(true));
        assertThat(actual.getState().isPresent(), is(false));
    }

    @Test
    public void requiredAndOptionalParamsShouldBeOK() throws Exception {
        Client c = loadClient();

        Map<String, List<String>>  p = makeParams(c);

        p.get("redirect_uri").add(c.getRedirectURI().toString());
        p.get("scope").add("profile");
        p.get("state").add("some-state");

        AuthRequest actual = validateImplicitGrant.run(p);

        assertThat(actual.getClientId(), is(c.getId()));
        assertThat(actual.getResponseTypes().size(), is(1));
        assertThat(actual.getResponseTypes().get(0), is((c.getResponseTypes().get(0).getName())));
        assertThat(actual.getRedirectURI().isPresent(), is(true));
        assertThat(actual.getRedirectURI().get(), is(c.getRedirectURI()));
        assertThat(actual.getScopes(), is(notNullValue()));
        assertThat(actual.getScopes().size(), is(1));
        assertThat(actual.getScopes().get(0), is("profile"));
        assertThat(actual.getState().isPresent(), is(true));
        assertThat(actual.getState().get(), is("some-state"));
    }
}
