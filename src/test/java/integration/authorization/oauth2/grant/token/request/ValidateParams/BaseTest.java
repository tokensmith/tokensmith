package integration.authorization.oauth2.grant.token.request.ValidateParams;

import helper.ValidateParamsAttributes;
import helper.fixture.persistence.LoadTokenClientWithScopes;
import org.junit.runner.RunWith;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.ValidateParams;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.exception.StateException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.persistence.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;

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
    protected ValidateParams validateParamsTokenResponseType;

    public Client loadClient() throws URISyntaxException {
        return loadTokenClientWithScopes.run();
    }

    public void runExpectInformResourceOwnerException(ValidateParamsAttributes p, Exception expectedDomainCause, int expectedErrorCode) {

        try {
            validateParamsTokenResponseType.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);
            fail(NO_EXCEPTION_EXPECTED_INFORM_RESOURCE_OWNER);
        } catch (InformResourceOwnerException e) {
            assertThat(e.getDomainCause(), instanceOf(expectedDomainCause.getClass()));
            assertThat(e.getCode(), is(expectedErrorCode));
        } catch(InformClientException e) {
            fail(EXPECTED_INFORM_RESOURCE_OWNER);
        }
    }

    public void runExpectInformResourceOwnerExceptionNoCause(ValidateParamsAttributes p, int expectedErrorCode) {

        try {
            validateParamsTokenResponseType.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);
            fail(NO_EXCEPTION_EXPECTED_INFORM_RESOURCE_OWNER);
        } catch (InformResourceOwnerException e) {
            assertThat(e.getDomainCause(), is(nullValue()));
            assertThat(e.getCode(), is(expectedErrorCode));
        } catch(InformClientException e) {
            fail(EXPECTED_INFORM_RESOURCE_OWNER);
        }
    }

    public void runExpectInformClientException(ValidateParamsAttributes p, Exception expectedDomainCause, int expectedErrorCode, String expectedError, String expectedDescription, URI expectedRedirect) {

        try {
            validateParamsTokenResponseType.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);
            fail(NO_EXCEPTION_EXPECTED_INFORM_CLIENT);
        } catch (InformClientException e) {
            assertThat(e.getDomainCause(), instanceOf(expectedDomainCause.getClass()));
            assertThat(e.getCode(), is(expectedErrorCode));
            assertThat(e.getError(), is(expectedError));
            assertThat(e.getDescription(), is(expectedDescription));
            assertThat(e.getRedirectURI(), is(expectedRedirect));
        } catch (InformResourceOwnerException e) {
            fail(EXPECTED_INFORM_CLIENT);
        }
    }

    public void runExpectInformClientExceptionNoCause(ValidateParamsAttributes p, int expectedErrorCode, String expectedError, String expectedDescription, URI expectedRedirect) throws StateException {

        try {
            validateParamsTokenResponseType.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);
            fail(NO_EXCEPTION_EXPECTED_INFORM_CLIENT);
        } catch (InformClientException e) {
            assertThat(e.getDomainCause(), is(nullValue()));
            assertThat(e.getCode(), is(expectedErrorCode));
            assertThat(e.getError(), is(expectedError));
            assertThat(e.getDescription(), is(expectedDescription));
            assertThat(e.getRedirectURI(), is(expectedRedirect));
        } catch (InformResourceOwnerException e) {
            fail(EXPECTED_INFORM_CLIENT);
        }
    }
}
