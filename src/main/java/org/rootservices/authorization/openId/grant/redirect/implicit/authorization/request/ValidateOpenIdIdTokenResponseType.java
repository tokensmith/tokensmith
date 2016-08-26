package org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request;

import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.factory.OpenIdTokenAuthRequestFactory;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.entity.OpenIdImplicitAuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tommackenzie on 7/23/16.
 */
@Component
public class ValidateOpenIdIdTokenResponseType {
    private OpenIdTokenAuthRequestFactory openIdTokenAuthRequestFactory;
    private ComparePublicClientToOpenIdAuthRequest comparePublicClientToOpenIdAuthRequest;

    @Autowired
    public ValidateOpenIdIdTokenResponseType(OpenIdTokenAuthRequestFactory openIdTokenAuthRequestFactory, ComparePublicClientToOpenIdAuthRequest comparePublicClientToOpenIdAuthRequest) {
        this.openIdTokenAuthRequestFactory = openIdTokenAuthRequestFactory;
        this.comparePublicClientToOpenIdAuthRequest = comparePublicClientToOpenIdAuthRequest;
    }

    public OpenIdImplicitAuthRequest run(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states, List<String> nonces) throws InformResourceOwnerException, InformClientException {
        OpenIdImplicitAuthRequest authRequest = openIdTokenAuthRequestFactory.make(clientIds, responseTypes, redirectUris, scopes, states, nonces);
        comparePublicClientToOpenIdAuthRequest.run(authRequest);

        return authRequest;
    }
}
