package integration.ValiateParams;

import helper.ValidateParamsAttributes;
import org.junit.runner.RunWith;
import org.rootservices.authorization.grant.ValidateParams;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.factory.exception.BaseException;
import org.rootservices.authorization.grant.code.factory.exception.StateException;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

/**
 * Created by tommackenzie on 3/1/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-auth-test.xml")
public abstract class BaseTest {

    @Autowired
    protected ClientRepository clientRepository;

    @Autowired
    protected ValidateParams subject;

    public void runExpectInformResourceOwnerException(ValidateParamsAttributes p, Exception expectedDomainCause, int expectedErrorCode) throws StateException {

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

    public void runExpectInformResourceOwnerExceptionNoCause(ValidateParamsAttributes p, int expectedErrorCode) throws StateException {

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

    public void runExpectInformClientException(ValidateParamsAttributes p, Exception expectedDomainCause, int expectedErrorCode, String expectedError, URI expectedRedirect) throws StateException {

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
