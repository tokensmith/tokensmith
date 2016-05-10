package integration.authorization.oauth2.grant.code.request.ValidateParams;

import helper.ValidateParamsAttributes;
import helper.fixture.persistence.LoadClientWithScopes;
import helper.fixture.persistence.LoadCodeClientWithScopes;
import helper.fixture.persistence.LoadCodeConfidentialClientWithScopes;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.ValidateParams;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.exception.StateException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.rootservices.authorization.persistence.repository.ClientScopesRepository;
import org.rootservices.authorization.persistence.repository.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

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
    protected ValidateParams subject;

    public Client loadConfidentialClient() throws URISyntaxException {
        ConfidentialClient cc = loadCodeConfidentialClientWithScopes.run();
        return cc.getClient();
    }

    public void runExpectInformResourceOwnerException(ValidateParamsAttributes p, Exception expectedDomainCause, int expectedErrorCode) {

        try {
            subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformResourceOwnerException e) {
            assertThat(e.getDomainCause().getClass().isInstance(expectedDomainCause)).isTrue();
            assertThat(e.getCode()).isEqualTo(expectedErrorCode);
        } catch(InformClientException e) {
            fail("InformClientException was thrown. Expected, InformResourceOwnerException");
        }
    }

    public void runExpectInformResourceOwnerExceptionNoCause(ValidateParamsAttributes p, int expectedErrorCode) {

        try {
            subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformResourceOwnerException e) {
            assertThat(e.getDomainCause()).isNull();
            assertThat(e.getCode()).isEqualTo(expectedErrorCode);
        } catch(InformClientException e) {
            fail("InformClientException was thrown. Expected, InformResourceOwnerException");
        }
    }

    public void runExpectInformClientException(ValidateParamsAttributes p, Exception expectedDomainCause, int expectedErrorCode, String expectedError, URI expectedRedirect) {

        try {
            subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformClientException e) {
            assertThat(e.getDomainCause().getClass().isInstance(expectedDomainCause)).isTrue();
            assertThat(e.getCode()).isEqualTo(expectedErrorCode);
            assertThat(e.getError()).isEqualTo(expectedError);
            assertThat(e.getRedirectURI().equals(expectedRedirect)).isTrue();
        } catch (InformResourceOwnerException e) {
            fail("InformResourceOwnerException was thrown. Expected, InformClientException");
        }
    }

    public void runExpectInformClientExceptionNoCause(ValidateParamsAttributes p, int expectedErrorCode, String expectedError, URI expectedRedirect) throws StateException {

        try {
            subject.run(p.clientIds, p.responseTypes, p.redirectUris, p.scopes, p.states);
            fail("expected InformResourceOwnerException to be thrown");
        } catch (InformClientException e) {
            assertThat(e.getDomainCause()).isNull();
            assertThat(e.getCode()).isEqualTo(expectedErrorCode);
            assertThat(e.getError()).isEqualTo(expectedError);
            assertThat(e.getRedirectURI().equals(expectedRedirect)).isTrue();
        } catch (InformResourceOwnerException e) {
            fail("InformResourceOwnerException was thrown. Expected, InformClientException");
        }
    }
}
