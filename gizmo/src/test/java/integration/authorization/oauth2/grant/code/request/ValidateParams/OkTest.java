package integration.authorization.oauth2.grant.code.request.ValidateParams;


import org.junit.Test;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.entity.AuthRequest;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


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
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c);

        AuthRequest actual = subject.run(p);

        assertThat(actual.getClientId(), is(c.getId()));
        assertThat(actual.getResponseTypes().size(),is(1));
        assertThat(actual.getResponseTypes().get(0), is(c.getResponseTypes().get(0).getName()));
        assertThat(actual.getRedirectURI().isPresent(), is(false));
        assertThat(actual.getScopes().isEmpty(), is(true));
        assertThat(actual.getState().isPresent(), is(false));
    }

    @Test
    public void requiredAndOptionalParamsShouldBeOK() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c);

        p.get("redirect_uri").add(c.getRedirectURI().toString());
        p.get("scope").add("profile");
        p.get("state").add("some-state");

        AuthRequest actual = subject.run(p);

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
