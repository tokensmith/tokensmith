package integration.ValiateParams.validation.Scopes;

import helper.FixtureFactory;
import helper.ValidateParamsAttributes;
import integration.ValiateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.grant.code.constant.ErrorCode;
import org.rootservices.authorization.grant.code.factory.exception.ResponseTypeException;
import org.rootservices.authorization.grant.code.factory.exception.ScopesException;
import org.rootservices.authorization.grant.code.factory.exception.StateException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.entity.Scope;

import java.net.URISyntaxException;

/**
 * /**
 * Scenario: Scopes fails validation And Client is found And redirects don't match.
 *
 * Given a client, c, exists in the db
 * And c's redirect uri is https://rootservices.org
 * And client ids has one item that is assigned to c's UUID
 * And redirect uris has one item that is assigned to https://rootservices.org/continue
 * And response types has one item that is CODE
 * And scopes is [method]
 * When the params are validated
 * Then raise a InformResourceOwner exception, e
 * And expect e's cause to be [expectedDomainCause]
 * And expects e's error code to be [errorCode]
 */
public class RedirectMismatchTest extends BaseTest {

    public static String REDIRECT_URI = "https://rootservices.org/continue";

    @Test
    public void invalid() throws URISyntaxException, StateException {
        Client c = FixtureFactory.makeClient();
        clientRepository.insert(c);

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.redirectUris.add(REDIRECT_URI);
        p.responseTypes.add(ResponseType.CODE.toString());

        p.scopes.add("invalid-scope");

        Exception expectedDomainCause = new ScopesException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void duplicate() throws URISyntaxException, StateException {
        Client c = FixtureFactory.makeClient();
        clientRepository.insert(c);

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.redirectUris.add(REDIRECT_URI);
        p.responseTypes.add(ResponseType.CODE.toString());

        p.scopes.add(Scope.PROFILE.toString());
        p.scopes.add(Scope.PROFILE.toString());

        Exception expectedDomainCause = new ScopesException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void emptyValue() throws URISyntaxException, StateException {
        Client c = FixtureFactory.makeClient();
        clientRepository.insert(c);

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.redirectUris.add(REDIRECT_URI);
        p.responseTypes.add(ResponseType.CODE.toString());

        p.scopes.add("");

        Exception expectedDomainCause = new ScopesException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

}
