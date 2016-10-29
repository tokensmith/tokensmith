package integration.authorization.oauth2.grant.code.request.ValidateParams;

import helper.ValidateParamsAttributes;
import helper.fixture.persistence.LoadCodeConfidentialClientWithScopes;
import org.junit.runner.RunWith;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.ValidateParams;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.StateException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;


import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by tommackenzie on 3/1/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-auth-test.xml")
@Transactional
public abstract class BaseTest {

    @Autowired
    private LoadCodeConfidentialClientWithScopes loadCodeConfidentialClientWithScopes;

    @Autowired
    protected ValidateParams validateParamsCodeResponseType;

    public Client loadConfidentialClient() throws URISyntaxException {
        ConfidentialClient cc = loadCodeConfidentialClientWithScopes.run();
        return cc.getClient();
    }

    public void runExpectInformResourceOwnerException(ValidateParamsAttributes p, Exception expectedDomainCause, int expectedErrorCode) {

        try {
            validateParamsCodeResponseType.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformResourceOwnerException e) {
            assertThat(e.getCause(), instanceOf(expectedDomainCause.getClass()));
            assertThat(e.getCode(), is(expectedErrorCode));
        } catch(InformClientException e) {
            fail("InformClientException was thrown. Expected, InformResourceOwnerException");
        }
    }

    public void runExpectInformResourceOwnerExceptionNoCause(ValidateParamsAttributes p, int expectedErrorCode) {

        try {
            validateParamsCodeResponseType.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformResourceOwnerException e) {
            assertThat(e.getCause(), is(nullValue()));
            assertThat(e.getCode(), is(expectedErrorCode));

        } catch(InformClientException e) {
            fail("InformClientException was thrown. Expected, InformResourceOwnerException");
        }
    }

    public void runExpectInformClientException(ValidateParamsAttributes p, Exception expectedDomainCause, int expectedErrorCode, String expectedError, String expectedDescription, URI expectedRedirect) {

        try {
            validateParamsCodeResponseType.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformClientException e) {
            assertThat(e.getCause(), instanceOf(expectedDomainCause.getClass()));
            assertThat(e.getCode(), is(expectedErrorCode));
            assertThat(e.getError(), is(expectedError));
            assertThat(e.getRedirectURI(), is(expectedRedirect));
            assertThat(e.getDescription(), is(expectedDescription));
        } catch (InformResourceOwnerException e) {
            fail("InformResourceOwnerException was thrown. Expected, InformClientException");
        }
    }

    public void runExpectInformClientExceptionNoCause(ValidateParamsAttributes p, int expectedErrorCode, String expectedError, String expectedDescription, URI expectedRedirect) throws StateException {

        try {
            validateParamsCodeResponseType.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformClientException e) {
            assertThat(e.getCause(), is(nullValue()));
            assertThat(e.getCode(), is(expectedErrorCode));
            assertThat(e.getError(), is(expectedError));
            assertThat(e.getRedirectURI(), is(expectedRedirect));
            assertThat(e.getDescription(), is(expectedDescription));
        } catch (InformResourceOwnerException e) {
            fail("InformResourceOwnerException was thrown. Expected, InformClientException");
        }
    }
}
