package org.rootservices.authorization.openId.grant.token.request.factory;

import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.code.authorization.request.entity.OpenIdAuthRequest;
import org.rootservices.authorization.openId.grant.code.authorization.request.factory.OpenIdAuthRequestFactory;
import org.rootservices.authorization.openId.grant.token.request.entity.OpenIdTokenAuthRequest;
import org.rootservices.authorization.openId.grant.token.request.factory.exception.NonceException;
import org.rootservices.authorization.openId.grant.token.request.factory.required.NonceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenIdTokenAuthRequestFactory {
    private OpenIdAuthRequestFactory openIdAuthRequestFactory;
    private NonceFactory nonceFactory;
    private static String INVALID_NONCE = "nonce is invalid";

    @Autowired
    public OpenIdTokenAuthRequestFactory(OpenIdAuthRequestFactory openIdAuthRequestFactory, NonceFactory nonceFactory) {
        this.openIdAuthRequestFactory = openIdAuthRequestFactory;
        this.nonceFactory = nonceFactory;
    }

    public OpenIdTokenAuthRequest make(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states, List<String> nonces) throws InformResourceOwnerException, InformClientException {

        String nonce;
        try {
            nonce = nonceFactory.makeNonce(nonces);
        } catch (NonceException e) {
            throw new InformResourceOwnerException(INVALID_NONCE, e, e.getCode());
        }

        OpenIdAuthRequest openIdAuthRequest = openIdAuthRequestFactory.make(clientIds, responseTypes, redirectUris, scopes, states);
        OpenIdTokenAuthRequest authRequest = translate(openIdAuthRequest);

        authRequest.setNonce(nonce);
        return authRequest;
    }

    private OpenIdTokenAuthRequest translate(OpenIdAuthRequest input) {
        OpenIdTokenAuthRequest output = new OpenIdTokenAuthRequest();
        output.setClientId(input.getClientId());
        output.setRedirectURI(input.getRedirectURI());
        output.setResponseTypes(input.getResponseTypes());
        output.setScopes(input.getScopes());
        output.setState(input.getState());

        return output;

    }
}
