package org.rootservices.authorization.oauth2.grant.redirect.code.authorization.request;

import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.CompareClientToAuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.ValidateParams;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.AuthRequestFactory;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.entity.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tommackenzie on 2/24/15.
 */
@Component
public class ValidateParamsCodeGrant implements ValidateParams {

    @Autowired
    private AuthRequestFactory authRequestFactory;
    @Autowired
    private CompareClientToAuthRequest compareClientToAuthRequest;

    public ValidateParamsCodeGrant() {
    }

    public ValidateParamsCodeGrant(AuthRequestFactory authRequestFactory, CompareClientToAuthRequest compareClientToAuthRequest) {
        this.authRequestFactory = authRequestFactory;
        this.compareClientToAuthRequest = compareClientToAuthRequest;
    }

    @Override
    public AuthRequest run(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states) throws InformResourceOwnerException, InformClientException {

        AuthRequest authRequest = authRequestFactory.makeAuthRequest(clientIds, responseTypes, redirectUris, scopes, states);
        compareClientToAuthRequest.run(authRequest);

        return authRequest;
    }
}
