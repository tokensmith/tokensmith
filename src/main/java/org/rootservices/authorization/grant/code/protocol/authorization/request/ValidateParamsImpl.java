package org.rootservices.authorization.grant.code.protocol.authorization.request;

import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.AuthRequestBuilder;
import org.rootservices.authorization.grant.code.protocol.authorization.request.entity.AuthRequest;
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
    private CompareClientToAuthRequest compareClientToAuthRequest;

    @Override
    public AuthRequest run(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states) throws InformResourceOwnerException, InformClientException {

        AuthRequest authRequest = authRequestBuilder.makeAuthRequest(clientIds, responseTypes, redirectUris, scopes, states);
        compareClientToAuthRequest.run(authRequest);

        return authRequest;
    }
}
