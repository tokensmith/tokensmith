package org.rootservices.authorization.openId.grant.redirect.code.authorization.request;

import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.factory.OpenIdCodeAuthRequestFactory;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.entity.OpenIdAuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tommackenzie on 10/10/15.
 */
@Component
public class ValidateOpenIdCodeResponseType {

    private OpenIdCodeAuthRequestFactory openIdCodeAuthRequestFactory;
    private CompareConfidentialClientToOpenIdAuthRequest compareConfidentialClientToOpenIdAuthRequest;

    @Autowired
    public ValidateOpenIdCodeResponseType(OpenIdCodeAuthRequestFactory openIdCodeAuthRequestFactory, CompareConfidentialClientToOpenIdAuthRequest compareConfidentialClientToOpenIdAuthRequest) {
        this.openIdCodeAuthRequestFactory = openIdCodeAuthRequestFactory;
        this.compareConfidentialClientToOpenIdAuthRequest = compareConfidentialClientToOpenIdAuthRequest;
    }

    public OpenIdAuthRequest run(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states) throws InformResourceOwnerException, InformClientException {
        OpenIdAuthRequest openIdAuthRequest = openIdCodeAuthRequestFactory.make(clientIds, responseTypes, redirectUris, scopes, states);
        compareConfidentialClientToOpenIdAuthRequest.run(openIdAuthRequest);

        return openIdAuthRequest;
    }
}