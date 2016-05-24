package org.rootservices.authorization.oauth2.grant.redirect.token.authorization.request;

import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.CompareClientToAuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.ValidateParams;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.AuthRequestBuilder;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.entity.AuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformResourceOwnerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tommackenzie on 5/22/16.
 */
@Component
public class ValidateParamsTokenResponseType implements ValidateParams {

    @Autowired
    private AuthRequestBuilder authRequestBuilderTokenResponseType;
    @Autowired
    private CompareClientToAuthRequest comparePublicClientToAuthRequest;

    public ValidateParamsTokenResponseType() {
    }

    public ValidateParamsTokenResponseType(AuthRequestBuilder authRequestBuilder, CompareClientToAuthRequest comparePublicClientToAuthRequest) {
        this.authRequestBuilderTokenResponseType = authRequestBuilder;
        this.comparePublicClientToAuthRequest = comparePublicClientToAuthRequest;
    }

    @Override
    public AuthRequest run(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states) throws InformResourceOwnerException, InformClientException {

        AuthRequest authRequest = authRequestBuilderTokenResponseType.makeAuthRequest(clientIds, responseTypes, redirectUris, scopes, states);
        comparePublicClientToAuthRequest.run(authRequest);

        return authRequest;
    }
}
