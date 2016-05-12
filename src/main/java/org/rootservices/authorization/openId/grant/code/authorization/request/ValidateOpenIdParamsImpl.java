package org.rootservices.authorization.openId.grant.code.authorization.request;

import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.code.authorization.request.builder.OpenIdAuthRequestBuilder;
import org.rootservices.authorization.openId.grant.code.authorization.request.entity.OpenIdAuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tommackenzie on 10/10/15.
 */
@Component
public class ValidateOpenIdParamsImpl implements ValidateOpenIdParams {

    private OpenIdAuthRequestBuilder openIdAuthRequestBuilder;
    private CompareConfidentialClientToOpenIdAuthRequest compareConfidentialClientToOpenIdAuthRequest;

    @Autowired
    public ValidateOpenIdParamsImpl(OpenIdAuthRequestBuilder openIdAuthRequestBuilder, CompareConfidentialClientToOpenIdAuthRequest compareConfidentialClientToOpenIdAuthRequest) {
        this.openIdAuthRequestBuilder = openIdAuthRequestBuilder;
        this.compareConfidentialClientToOpenIdAuthRequest = compareConfidentialClientToOpenIdAuthRequest;
    }

    @Override
    public OpenIdAuthRequest run(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states) throws InformResourceOwnerException, InformClientException {
        OpenIdAuthRequest openIdAuthRequest = openIdAuthRequestBuilder.build(clientIds, responseTypes, redirectUris, scopes, states);
        compareConfidentialClientToOpenIdAuthRequest.run(openIdAuthRequest);

        return openIdAuthRequest;
    }
}
