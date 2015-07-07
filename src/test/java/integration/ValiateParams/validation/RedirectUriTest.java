package integration.ValiateParams.validation;

import helper.ValidateParamsAttributes;
import integration.ValiateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.authorization.factory.exception.RedirectUriException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.factory.exception.StateException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.net.URISyntaxException;


/**
 * Scenario: Redirect URIs fails validation And Client is found.
 *
 * Given a client, c, exists in the db
 * And c's response type is CODE
 * And c's redirect uri is https://rootservices.org
 * And client ids has one item that is assigned to c's UUID
 * And response types has one item that is CODE
 * And redirect uris has one item that is assigned [x]
 * When the params are validated
 * Then raise a InformClientException exception, e
 * And expects e's error code to be [errorCode]
 * And expects e's redirect uri to be c's redirect uri
 */
public class RedirectUriTest extends BaseTest {


    /**
     * Then expect e's cause to be [expectedDomainCause]
     *
     * @throws URISyntaxException
     * @throws StateException
     */
    @Test
    public void emptyList() throws URISyntaxException, StateException {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add(ResponseType.CODE.toString());
        p.redirectUris.add("");

        Exception expectedDomainCause = new RedirectUriException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_EMPTY_VALUE.getCode();

        runExpectInformResourceOwnerException(p,expectedDomainCause,expectedErrorCode);
    }

    /**
     * Then expect e's cause to be [expectedDomainCause]
     *
     * @throws URISyntaxException
     * @throws StateException
     */
    @Test
    public void duplicate() throws URISyntaxException, StateException {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add(ResponseType.CODE.toString());
        p.redirectUris.add(c.getRedirectURI().toString());
        p.redirectUris.add(c.getRedirectURI().toString());

        Exception expectedDomainCause = new RedirectUriException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MORE_THAN_ONE_ITEM.getCode();

        runExpectInformResourceOwnerException(p,expectedDomainCause,expectedErrorCode);
    }

    /**
     * Then expect e's cause to be [expectedDomainCause]
     *
     * @throws URISyntaxException
     * @throws StateException
     */
    @Test
    public void invalid() throws URISyntaxException, StateException {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add(ResponseType.CODE.toString());
        p.redirectUris.add("invalid-uri");

        Exception expectedDomainCause = new RedirectUriException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_DATA_TYPE.getCode();

        runExpectInformResourceOwnerException(p,expectedDomainCause,expectedErrorCode);
    }

    /**
     * Then expect e's cause to be [expectedDomainCause]
     *
     * @throws URISyntaxException
     * @throws StateException
     */
    public void notHttps() throws URISyntaxException, StateException {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add(ResponseType.CODE.toString());
        p.redirectUris.add("http://rootservices.org");

        Exception expectedDomainCause = new RedirectUriException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_DATA_TYPE.getCode();

        runExpectInformResourceOwnerException(p,expectedDomainCause,expectedErrorCode);
    }

    /**
     * Then expect e's cause to be null
     *
     * @throws URISyntaxException
     * @throws StateException
     */
    @Test
    public void mismatches() throws URISyntaxException, StateException {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add(ResponseType.CODE.toString());
        p.redirectUris.add("https://rootservices.org/continue");

        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerExceptionNoCause(p, expectedErrorCode);
    }
}
