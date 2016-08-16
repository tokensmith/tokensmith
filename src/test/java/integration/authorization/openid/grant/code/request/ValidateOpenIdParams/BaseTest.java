package integration.authorization.openid.grant.code.request.ValidateOpenIdParams;

import helper.ValidateParamsAttributes;
import helper.fixture.persistence.openid.LoadCodeConfidentialClientWithOpenIdScope;
import org.junit.runner.RunWith;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.StateException;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.ValidateOpenIdCodeResponseType;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

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
    private LoadCodeConfidentialClientWithOpenIdScope loadCodeConfidentialClientWithOpenIdScope;
    @Autowired
    protected ValidateOpenIdCodeResponseType subject;

    public Client loadConfidentialClient() throws Exception {
        ConfidentialClient cc = loadCodeConfidentialClientWithOpenIdScope.run();
        return cc.getClient();
    }

    public void runExpectInformResourceOwnerException(ValidateParamsAttributes p, Exception expectedDomainCause, int expectedErrorCode) {

        try {
            subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformResourceOwnerException e) {
            assertThat(e.getDomainCause(), instanceOf(expectedDomainCause.getClass()));
            assertThat(e.getCode(), is(expectedErrorCode));
        } catch(InformClientException e) {
            fail("InformClientException was thrown. Expected, InformResourceOwnerException");
        }
    }

    public void runExpectInformResourceOwnerExceptionNoCause(ValidateParamsAttributes p, int expectedErrorCode) {

        try {
            subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformResourceOwnerException e) {
            assertThat(e.getDomainCause(), is(nullValue()));
            assertThat(e.getCode(), is(expectedErrorCode));
        } catch(InformClientException e) {
            fail("InformClientException was thrown. Expected, InformResourceOwnerException");
        }
    }

    public void runExpectInformClientException(ValidateParamsAttributes p, Exception expectedDomainCause, int expectedErrorCode, String expectedError, String expectedDescription, URI expectedRedirect) {

        try {
            subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformClientException e) {
            assertThat(e.getDomainCause(), instanceOf(expectedDomainCause.getClass()));
            assertThat(e.getCode(), is(expectedErrorCode));
            assertThat(e.getError(), is(expectedError));
            assertThat(e.getRedirectURI(), is(expectedRedirect));
        } catch (InformResourceOwnerException e) {
            fail("InformResourceOwnerException was thrown. Expected, InformClientException");
        }
    }

    public void runExpectInformClientExceptionNoCause(ValidateParamsAttributes p, int expectedErrorCode, String expectedError, String expectedDescription, URI expectedRedirect) throws StateException {

        try {
            subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformClientException e) {
            assertThat(e.getDomainCause(), is(nullValue()));
            assertThat(e.getCode(), is(expectedErrorCode));
            assertThat(e.getError(), is(expectedError));
            assertThat(e.getDescription(), is(expectedDescription));
            assertThat(e.getRedirectURI(), is(expectedRedirect));
        } catch (InformResourceOwnerException e) {
            fail("InformResourceOwnerException was thrown. Expected, InformClientException");
        }
    }
}
