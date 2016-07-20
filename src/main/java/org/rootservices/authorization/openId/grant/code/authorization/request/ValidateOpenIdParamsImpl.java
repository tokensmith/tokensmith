package org.rootservices.authorization.openId.grant.code.authorization.request;

import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.code.authorization.request.factory.OpenIdAuthRequestFactory;
import org.rootservices.authorization.openId.grant.code.authorization.request.entity.OpenIdAuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tommackenzie on 10/10/15.
 */
@Component
public class ValidateOpenIdParamsImpl implements ValidateOpenIdParams {

    private OpenIdAuthRequestFactory openIdAuthRequestFactory;
    private CompareConfidentialClientToOpenIdAuthRequest compareConfidentialClientToOpenIdAuthRequest;

    @Autowired
    public ValidateOpenIdParamsImpl(OpenIdAuthRequestFactory openIdAuthRequestFactory, CompareConfidentialClientToOpenIdAuthRequest compareConfidentialClientToOpenIdAuthRequest) {
        this.openIdAuthRequestFactory = openIdAuthRequestFactory;
        this.compareConfidentialClientToOpenIdAuthRequest = compareConfidentialClientToOpenIdAuthRequest;
    }

    @Override
    public OpenIdAuthRequest run(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states) throws InformResourceOwnerException, InformClientException {
        OpenIdAuthRequest openIdAuthRequest = openIdAuthRequestFactory.make(clientIds, responseTypes, redirectUris, scopes, states);
        compareConfidentialClientToOpenIdAuthRequest.run(openIdAuthRequest);

        return openIdAuthRequest;
    }
}
