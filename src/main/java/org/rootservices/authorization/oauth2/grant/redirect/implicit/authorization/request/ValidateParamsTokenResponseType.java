package org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.request;

import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.CompareClientToAuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.ValidateParams;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.AuthRequestFactory;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.entity.AuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tommackenzie on 5/22/16.
 */
@Component
public class ValidateParamsTokenResponseType implements ValidateParams {

    @Autowired
    private AuthRequestFactory authRequestFactoryTokenResponseType;
    @Autowired
    private CompareClientToAuthRequest comparePublicClientToAuthRequest;

    public ValidateParamsTokenResponseType() {
    }

    public ValidateParamsTokenResponseType(AuthRequestFactory authRequestFactoryTokenResponseType, CompareClientToAuthRequest comparePublicClientToAuthRequest) {
        this.authRequestFactoryTokenResponseType = authRequestFactoryTokenResponseType;
        this.comparePublicClientToAuthRequest = comparePublicClientToAuthRequest;
    }

    @Override
    public AuthRequest run(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states) throws InformResourceOwnerException, InformClientException {

        AuthRequest authRequest = authRequestFactoryTokenResponseType.makeAuthRequest(clientIds, responseTypes, redirectUris, scopes, states);
        comparePublicClientToAuthRequest.run(authRequest);

        return authRequest;
    }
}
