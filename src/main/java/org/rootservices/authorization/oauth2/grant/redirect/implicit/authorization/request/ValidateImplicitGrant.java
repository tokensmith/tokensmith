package org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.request;

import org.apache.commons.validator.routines.UrlValidator;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.ValidateRequest;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.context.GetClientRedirectUri;
import org.rootservices.authorization.parse.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 5/1/17.
 */
@Component
public class ValidateImplicitGrant extends ValidateRequest {

    @Autowired
    public ValidateImplicitGrant(Parser parser, UrlValidator urlValidator, GetClientRedirectUri getPublicClientRedirectUri, ComparePublicClientToAuthRequest comparePublicClientToAuthRequest) {
        super(parser, urlValidator, getPublicClientRedirectUri, comparePublicClientToAuthRequest);
    }
}
