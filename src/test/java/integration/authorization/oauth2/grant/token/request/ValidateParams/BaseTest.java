package integration.authorization.oauth2.grant.token.request.ValidateParams;


import helper.fixture.persistence.LoadTokenClientWithScopes;
import org.junit.runner.RunWith;
import org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.request.ValidateImplicitGrant;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.persistence.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;


/**
 * Created by tommackenzie on 5/20/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-auth-test.xml")
@Transactional
public abstract class BaseTest {

    private static String EXPECTED_INFORM_CLIENT = "InformResourceOwnerException was thrown. Expected, InformClientException";
    private static String NO_EXCEPTION_EXPECTED_INFORM_CLIENT = "Expected InformClientException to be thrown";
    private static String EXPECTED_INFORM_RESOURCE_OWNER = "InformClientException was thrown. Expected, InformResourceOwnerException";
    private static String NO_EXCEPTION_EXPECTED_INFORM_RESOURCE_OWNER = "Expected InformResourceOwnerException to be thrown";

    @Autowired
    private LoadTokenClientWithScopes loadTokenClientWithScopes;

    @Autowired
    protected ValidateImplicitGrant validateImplicitGrant;

    public Map<String, List<String>> makeParams() {
        Map<String, List<String>> parameters = new HashMap<>();

        List<String> clientIds = new ArrayList();
        List<String> responseTypes = new ArrayList<>();
        List<String> redirectUris = new ArrayList<>();
        List<String> scopes = new ArrayList<>();
        List<String> states = new ArrayList<>();

        parameters.put("client_id", clientIds);
        parameters.put("response_type", responseTypes);
        parameters.put("redirect_uri", redirectUris);
        parameters.put("scope", scopes);
        parameters.put("state", states);

        return parameters;
    }

    public Client loadClient() throws URISyntaxException {
        return loadTokenClientWithScopes.run();
    }

    public void runExpectInformResourceOwnerException(Map<String, List<String>> p, Exception expectedDomainCause, int expectedErrorCode) {

        try {
            validateImplicitGrant.run(p);
            fail(NO_EXCEPTION_EXPECTED_INFORM_RESOURCE_OWNER);
        } catch (InformResourceOwnerException e) {
            assertThat(e.getCause(), instanceOf(expectedDomainCause.getClass()));
            assertThat(e.getCode(), is(expectedErrorCode));
        } catch(InformClientException e) {
            fail(EXPECTED_INFORM_RESOURCE_OWNER);
        }
    }

    public void runExpectInformResourceOwnerExceptionNoCause(Map<String, List<String>> p, int expectedErrorCode) {

        try {
            validateImplicitGrant.run(p);
            fail(NO_EXCEPTION_EXPECTED_INFORM_RESOURCE_OWNER);
        } catch (InformResourceOwnerException e) {
            assertThat(e.getCause(), is(nullValue()));
            assertThat(e.getCode(), is(expectedErrorCode));
        } catch(InformClientException e) {
            fail(EXPECTED_INFORM_RESOURCE_OWNER);
        }
    }

    public void runExpectInformClientExceptionWithState(Map<String, List<String>> p, Exception expectedDomainCause, int expectedErrorCode, String expectedError, String expectedDescription, URI expectedRedirect) {

        try {
            validateImplicitGrant.run(p);
            fail(NO_EXCEPTION_EXPECTED_INFORM_CLIENT);
        } catch (InformClientException e) {
            assertThat(e.getCause(), instanceOf(expectedDomainCause.getClass()));
            assertThat(e.getCode(), is(expectedErrorCode));
            assertThat(e.getError(), is(expectedError));
            assertThat(e.getDescription(), is(expectedDescription));
            assertThat(e.getRedirectURI(), is(expectedRedirect));
            assertThat(e.getState().isPresent(), is(true));
            assertThat(e.getState().get(), is(p.get("state").get(0)));
        } catch (InformResourceOwnerException e) {
            fail(EXPECTED_INFORM_CLIENT);
        }
    }

    public void runExpectInformClientException(Map<String, List<String>> p, Exception expectedDomainCause, int expectedErrorCode, String expectedError, String expectedDescription, URI expectedRedirect) {

        try {
            validateImplicitGrant.run(p);
            fail(NO_EXCEPTION_EXPECTED_INFORM_CLIENT);
        } catch (InformClientException e) {
            assertThat(e.getCause(), instanceOf(expectedDomainCause.getClass()));
            assertThat(e.getCode(), is(expectedErrorCode));
            assertThat(e.getError(), is(expectedError));
            assertThat(e.getDescription(), is(expectedDescription));
            assertThat(e.getRedirectURI(), is(expectedRedirect));
            assertThat(e.getState().isPresent(), is(false));
        } catch (InformResourceOwnerException e) {
            fail(EXPECTED_INFORM_CLIENT);
        }
    }

    public void runExpectInformClientExceptionWithStateNoCause(Map<String, List<String>> p, int expectedErrorCode, String expectedError, String expectedDescription, URI expectedRedirect) throws Exception {

        try {
            validateImplicitGrant.run(p);
            fail(NO_EXCEPTION_EXPECTED_INFORM_CLIENT);
        } catch (InformClientException e) {
            assertThat(e.getCause(), is(nullValue()));
            assertThat(e.getCode(), is(expectedErrorCode));
            assertThat(e.getError(), is(expectedError));
            assertThat(e.getDescription(), is(expectedDescription));
            assertThat(e.getRedirectURI(), is(expectedRedirect));
            assertThat(e.getState().isPresent(), is(true));
            assertThat(e.getState().get(), is(p.get("state").get(0)));
        } catch (InformResourceOwnerException e) {
            fail(EXPECTED_INFORM_CLIENT);
        }
    }
}
