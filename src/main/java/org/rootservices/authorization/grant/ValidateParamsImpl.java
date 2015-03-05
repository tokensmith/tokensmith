package org.rootservices.authorization.grant;

import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.factory.AuthRequestFactory;
import org.rootservices.authorization.grant.code.factory.exception.StateException;
import org.rootservices.authorization.grant.code.factory.optional.StateFactory;
import org.rootservices.authorization.grant.code.request.AuthRequest;
import org.rootservices.authorization.grant.code.request.ValidateAuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 2/24/15.
 */
@Component
public class ValidateParamsImpl implements ValidateParams {

    @Autowired
    private AuthRequestFactory authRequestFactory;

    @Autowired
    private StateFactory stateFactory;

    @Autowired
    private ValidateAuthRequest validateAuthRequest;

    @Override
    public boolean run(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states) throws InformResourceOwnerException, InformClientException, StateException {

        AuthRequest authRequest = null;
        authRequest = authRequestFactory.makeAuthRequest(clientIds, responseTypes, redirectUris, scopes);

        Optional<String> cleanedStates;
        cleanedStates = stateFactory.makeState(states);

        validateAuthRequest.run(authRequest);

        return true;
    }
}
