package org.rootservices.authorization.openId.grant.redirect.code.authorization.request;

import org.apache.commons.validator.routines.UrlValidator;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.context.GetOpenIdConfidentialClientRedirectUri;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.entity.OpenIdAuthRequest;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.ValidateOpenIdRequest;

import org.rootservices.authorization.parse.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by tommackenzie on 10/10/15.
 */
@Component
public class ValidateOpenIdCodeResponseType extends ValidateOpenIdRequest<OpenIdAuthRequest> {

    @Autowired
    public ValidateOpenIdCodeResponseType(Parser parser, UrlValidator urlValidator, GetOpenIdConfidentialClientRedirectUri getOpenIdConfidentialClientRedirectUri, CompareConfidentialClientToOpenIdAuthRequest compareConfidentialClientToOpenIdAuthRequest) {
        super(parser, urlValidator, getOpenIdConfidentialClientRedirectUri, compareConfidentialClientToOpenIdAuthRequest);
    }
}
