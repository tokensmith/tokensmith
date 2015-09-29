package org.rootservices.authorization.grant.code.protocol.authorization.request;

import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.AuthRequestBuilder;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.StateException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.optional.StateBuilder;
import org.rootservices.authorization.grant.code.protocol.authorization.request.entity.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 2/24/15.
 */
@Component
public class ValidateParamsImpl implements ValidateParams {

    @Autowired
    private AuthRequestBuilder authRequestBuilder;

    @Autowired
    private ValidateAuthRequest validateAuthRequest;

    @Override
    public AuthRequest run(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states) throws InformResourceOwnerException, InformClientException {

        AuthRequest authRequest = authRequestBuilder.makeAuthRequest(clientIds, responseTypes, redirectUris, scopes, states);
        validateAuthRequest.run(authRequest);

        return authRequest;
    }
}
