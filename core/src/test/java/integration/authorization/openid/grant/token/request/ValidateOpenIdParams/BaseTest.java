package integration.authorization.openid.grant.token.request.ValidateOpenIdParams;


import helper.fixture.persistence.openid.LoadTokenClientWithOpenIdScope;
import org.junit.runner.RunWith;
import net.tokensmith.authorization.exception.ServerException;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.request.ValidateOpenIdIdImplicitGrant;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.*;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;


/**
 * Created by tommackenzie on 3/1/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-auth-test.xml")
@Transactional
public abstract class BaseTest {

    @Autowired
    private LoadTokenClientWithOpenIdScope loadTokenClientWithOpenIdScope;
    @Autowired
    protected ValidateOpenIdIdImplicitGrant subject;

    public Client loadClient() throws Exception {
        // TODO: this may also need the response type, id_token.
        Client c = loadTokenClientWithOpenIdScope.run();
        return c;
    }

    public Map<String, List<String>> makeParams() {
        Map<String, List<String>> parameters = new HashMap<>();

        List<String> clientIds = new ArrayList<>();
        List<String> responseTypes = new ArrayList<>();
        List<String> redirectUris = new ArrayList<>();
        List<String> scopes = new ArrayList<>();
        List<String> states = new ArrayList<>();
        List<String> nonces = new ArrayList<>();

        parameters.put("client_id", clientIds);
        parameters.put("response_type", responseTypes);
        parameters.put("redirect_uri", redirectUris);
        parameters.put("scope", scopes);
        parameters.put("state", states);
        parameters.put("nonce", nonces);


        return parameters;
    }

    public Map<String, List<String>> makeParamsWithNonce(Client client) {
        Map<String, List<String>> params = makeParams();

        params.get("client_id").add(client.getId().toString());
        for(ResponseType responseType: client.getResponseTypes()) {
            params.get("response_type").add(responseType.getName());
        }

        params.get("redirect_uri").add(client.getRedirectURI().toString());
        params.get("scope").add(client.getScopes().get(0).getName());
        params.get("state").add("some-state");
        params.get("nonce").add("some-nonce");
        return params;
    }

    public Map<String, List<String>> makeParamsWithNonceRequiredOnly(Client client) {
        Map<String, List<String>> params = makeParams();

        params.get("client_id").add(client.getId().toString());
        for(ResponseType responseType: client.getResponseTypes()) {
            params.get("response_type").add(responseType.getName());
        }

        params.get("redirect_uri").add(client.getRedirectURI().toString());

        List<String> nonces = new ArrayList<>();
        params.get("nonce").add("some-nonce");
        return params;
    }

    public Map<String, List<String>> makeParamsWithNonce() {
        Map<String, List<String>> params = makeParams();

        params.get("client_id").add(UUID.randomUUID().toString());
        params.get("response_type").add("TOKEN");
        params.get("redirect_uri").add("https://tokensmith.net");
        params.get("nonce").add("some-nonce");

        return params;
    }

    public void runExpectInformResourceOwnerException(Map<String, List<String>> p, Exception expectedDomainCause, int expectedErrorCode) {

        try {
            subject.run(p);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformResourceOwnerException e) {
            assertThat(e.getCause(), instanceOf(expectedDomainCause.getClass()));
            assertThat(e.getCode(), is(expectedErrorCode));
        } catch(InformClientException e) {
            fail("InformClientException was thrown. Expected, InformResourceOwnerException");
        } catch (ServerException e) {
            fail("ServerException was thrown. Expected, InformResourceOwnerException");
        }
    }

    public void runExpectInformResourceOwnerExceptionNoCause(Map<String, List<String>> p, int expectedErrorCode) {

        try {
            subject.run(p);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformResourceOwnerException e) {
            assertThat(e.getCause(), is(nullValue()));
            assertThat(e.getCode(), is(expectedErrorCode));
        } catch(InformClientException e) {
            fail("InformClientException was thrown. Expected, InformResourceOwnerException");
        } catch (ServerException e) {
            fail("ServerException was thrown. Expected, InformResourceOwnerException");
        }
    }

    public void runExpectInformClientExceptionWithState(Map<String, List<String>> p, Exception cause, int expectedErrorCode, String expectedError, String expectedDescription, URI expectedRedirect) {

        try {
            subject.run(p);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformClientException e) {
            assertThat(e.getCause(), instanceOf(cause.getClass()));
            assertThat(e.getCode(), is(expectedErrorCode));
            assertThat(e.getError(), is(expectedError));
            assertThat(e.getRedirectURI(), is(expectedRedirect));
            assertThat(e.getState().isPresent(), is(true));
            assertThat(e.getState().get(), is(p.get("state").get(0)));
            assertThat(e.getDescription(), is(expectedDescription));
        } catch (InformResourceOwnerException e) {
            fail("InformResourceOwnerException was thrown. Expected, InformClientException");
        } catch (ServerException e) {
            fail("ServerException was thrown. Expected, InformClientException");
        }
    }

    public void runExpectInformClientException(Map<String, List<String>> p, Exception expectedDomainCause, int expectedErrorCode, String expectedError, String expectedDescription, URI expectedRedirect) {

        try {
            subject.run(p);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformClientException e) {
            assertThat(e.getCause(), instanceOf(expectedDomainCause.getClass()));
            assertThat(e.getCode(), is(expectedErrorCode));
            assertThat(e.getError(), is(expectedError));
            assertThat(e.getRedirectURI(), is(expectedRedirect));
            assertThat(e.getState().isPresent(), is(false));
        } catch (InformResourceOwnerException e) {
            fail("InformResourceOwnerException was thrown. Expected, InformClientException");
        } catch (ServerException e) {
            fail("ServerException was thrown. Expected, InformClientException");
        }
    }

    public void runExpectInformClientExceptionWithStateNoCause(Map<String, List<String>> p, int expectedErrorCode, String expectedError, String expectedDescription, URI expectedRedirect) throws Exception {

        try {
            subject.run(p);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformClientException e) {
            assertThat(e.getCause(), is(nullValue()));
            assertThat(e.getCode(), is(expectedErrorCode));
            assertThat(e.getError(), is(expectedError));
            assertThat(e.getDescription(), is(expectedDescription));
            assertThat(e.getRedirectURI(), is(expectedRedirect));
            assertThat(e.getState().isPresent(), is(true));
            assertThat(e.getState().get(), is(p.get("state").get(0)));
        } catch (InformResourceOwnerException e) {
            fail("InformResourceOwnerException was thrown. Expected, InformClientException");
        }
    }
}
