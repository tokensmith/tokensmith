package org.rootservices.authorization.oauth2.grant.redirect.code.authorization.request;

import org.apache.commons.validator.routines.UrlValidator;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.ValidateRequest;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.context.GetClientRedirectUri;
import org.rootservices.authorization.parse.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 4/12/17.
 */
@Component
public class ValidateCodeGrant extends ValidateRequest {

    @Autowired
    public ValidateCodeGrant(Parser parser, UrlValidator urlValidator, GetClientRedirectUri getConfidentialClientRedirectUri, CompareConfidentialClientToAuthRequest compareConfidentialClientToAuthRequest) {
        super(parser, urlValidator, getConfidentialClientRedirectUri, compareConfidentialClientToAuthRequest);
    }
}
