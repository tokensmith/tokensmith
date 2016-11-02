package integration.authorization.openid.grant.token.request.ValidateOpenIdParams;

import helper.ValidateParamsWithNonce;
import helper.fixture.persistence.openid.LoadTokenClientWithOpenIdScope;
import org.junit.runner.RunWith;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.StateException;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.ValidateOpenIdIdImplicitGrant;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.UUID;

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

    public ValidateParamsWithNonce makeValidateParamsWithNonce(Client client) {
        ValidateParamsWithNonce p = new ValidateParamsWithNonce();
        p.clientIds.add(client.getId().toString());

        for(ResponseType responseType: client.getResponseTypes()) {
            p.responseTypes.add(responseType.getName());
        }

        p.redirectUris.add(client.getRedirectURI().toString());
        p.scopes.add(client.getScopes().get(0).getName());
        p.states.add("some-state");
        p.nonces.add("some-nonce");

        return p;
    }

    public ValidateParamsWithNonce makeValidateParamsWithNonceRequiredOnly(Client client) {
        ValidateParamsWithNonce p = new ValidateParamsWithNonce();

        p.clientIds.add(client.getId().toString());
        p.redirectUris.add(client.getRedirectURI().toString());
        for(ResponseType responseType: client.getResponseTypes()) {
            p.responseTypes.add(responseType.getName());
        }
        p.nonces.add("some-nonce");

        return p;
    }

    public ValidateParamsWithNonce makeValidateParamsWithNonce() {
        ValidateParamsWithNonce p = new ValidateParamsWithNonce();

        p.clientIds.add(UUID.randomUUID().toString());
        p.redirectUris.add("https://rootservices.org");
        p.responseTypes.add("TOKEN");
        p.nonces.add("some-nonce");

        return p;

    }

    public void runExpectInformResourceOwnerException(ValidateParamsWithNonce p, Exception expectedDomainCause, int expectedErrorCode) {

        try {
            subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states, p.nonces);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformResourceOwnerException e) {
            assertThat(e.getCause(), instanceOf(expectedDomainCause.getClass()));
            assertThat(e.getCode(), is(expectedErrorCode));
        } catch(InformClientException e) {
            fail("InformClientException was thrown. Expected, InformResourceOwnerException");
        }
    }

    public void runExpectInformResourceOwnerExceptionNoCause(ValidateParamsWithNonce p, int expectedErrorCode) {

        try {
            subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states, p.nonces);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformResourceOwnerException e) {
            assertThat(e.getCause(), is(nullValue()));
            assertThat(e.getCode(), is(expectedErrorCode));
        } catch(InformClientException e) {
            fail("InformClientException was thrown. Expected, InformResourceOwnerException");
        }
    }

    public void runExpectInformClientExceptionWithState(ValidateParamsWithNonce p, Exception expectedDomainCause, int expectedErrorCode, String expectedError, String expectedDescription, URI expectedRedirect) {

        try {
            subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states, p.nonces);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformClientException e) {
            assertThat(e.getCause(), instanceOf(expectedDomainCause.getClass()));
            assertThat(e.getCode(), is(expectedErrorCode));
            assertThat(e.getError(), is(expectedError));
            assertThat(e.getRedirectURI(), is(expectedRedirect));
            assertThat(e.getState().isPresent(), is(true));
            assertThat(e.getState().get(), is(p.states.get(0)));
        } catch (InformResourceOwnerException e) {
            fail("InformResourceOwnerException was thrown. Expected, InformClientException");
        }
    }

    public void runExpectInformClientException(ValidateParamsWithNonce p, Exception expectedDomainCause, int expectedErrorCode, String expectedError, String expectedDescription, URI expectedRedirect) {

        try {
            subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states, p.nonces);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformClientException e) {
            assertThat(e.getCause(), instanceOf(expectedDomainCause.getClass()));
            assertThat(e.getCode(), is(expectedErrorCode));
            assertThat(e.getError(), is(expectedError));
            assertThat(e.getRedirectURI(), is(expectedRedirect));
            assertThat(e.getState().isPresent(), is(false));
        } catch (InformResourceOwnerException e) {
            fail("InformResourceOwnerException was thrown. Expected, InformClientException");
        }
    }

    public void runExpectInformClientExceptionWithStateNoCause(ValidateParamsWithNonce p, int expectedErrorCode, String expectedError, String expectedDescription, URI expectedRedirect) throws StateException {

        try {
            subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states, p.nonces);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformClientException e) {
            assertThat(e.getCause(), is(nullValue()));
            assertThat(e.getCode(), is(expectedErrorCode));
            assertThat(e.getError(), is(expectedError));
            assertThat(e.getDescription(), is(expectedDescription));
            assertThat(e.getRedirectURI(), is(expectedRedirect));
            assertThat(e.getState().isPresent(), is(true));
            assertThat(e.getState().get(), is(p.states.get(0)));
        } catch (InformResourceOwnerException e) {
            fail("InformResourceOwnerException was thrown. Expected, InformClientException");
        }
    }
}
