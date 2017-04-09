package org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request;

import org.apache.commons.validator.routines.UrlValidator;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.context.GetOpenIdPublicClientRedirectUri;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.factory.OpenIdTokenAuthRequestFactory;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.entity.OpenIdImplicitAuthRequest;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.CompareClientToOpenIdAuthRequest;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.ValidateOpenIdRequest;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.context.GetOpenIdClientRedirectUri;
import org.rootservices.authorization.parse.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tommackenzie on 7/23/16.
 */
@Component
public class ValidateOpenIdIdImplicitGrant extends ValidateOpenIdRequest<OpenIdImplicitAuthRequest> {

    @Autowired
    public ValidateOpenIdIdImplicitGrant(Parser parser, UrlValidator urlValidator, GetOpenIdPublicClientRedirectUri getOpenIdPublicClientRedirectUri, ComparePublicClientToOpenIdAuthRequest comparePublicClientToOpenIdAuthRequest) {
        super(parser, urlValidator, getOpenIdPublicClientRedirectUri, comparePublicClientToOpenIdAuthRequest);
    }

}
