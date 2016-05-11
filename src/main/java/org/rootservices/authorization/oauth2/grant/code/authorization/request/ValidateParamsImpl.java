package org.rootservices.authorization.oauth2.grant.code.authorization.request;

import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.AuthRequestBuilder;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.entity.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tommackenzie on 2/24/15.
 *
 * This will be used for the response types:
 * - code
 * - token
 */
@Component
public class ValidateParamsImpl implements ValidateParams {

    @Autowired
    private AuthRequestBuilder authRequestBuilder;

    @Autowired
    private CompareConfidentialClientToAuthRequest compareConfidentialClientToAuthRequest;

    @Override
    public AuthRequest run(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states) throws InformResourceOwnerException, InformClientException {

        AuthRequest authRequest = authRequestBuilder.makeAuthRequest(clientIds, responseTypes, redirectUris, scopes, states);
        compareConfidentialClientToAuthRequest.run(authRequest);

        return authRequest;
    }
}
