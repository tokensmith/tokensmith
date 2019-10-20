package org.rootservices.authorization.http.controller.resource.html.authorization.helper;


import org.rootservices.authorization.http.controller.resource.html.authorization.oauth.OAuth2CodeResource;
import org.rootservices.authorization.http.controller.resource.html.authorization.oauth.OAuth2ImplicitResource;
import org.rootservices.authorization.http.controller.resource.html.authorization.openid.OpenIdCodeResource;
import org.rootservices.authorization.http.controller.resource.html.authorization.openid.OpenIdImplicitIdentityResource;
import org.rootservices.authorization.http.controller.resource.html.authorization.openid.OpenIdImplicitResource;
import org.rootservices.authorization.http.controller.security.TokenSession;
import org.rootservices.authorization.http.controller.security.WebSiteUser;
import org.rootservices.otter.controller.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthorizationFactory {

    private static String OPENID_SCOPE = "openid";
    private static String IMPLICIT_GRANT = "token";
    private static String AUTHORIZATION_CODE_GRANT = "code";
    private static String IDENTITY_RESPONSE_TYPE = "id_token";

    // oauth2
    private OAuth2CodeResource oAuth2CodeResource;
    private OAuth2ImplicitResource oAuth2ImplicitResource;

    // openid2
    private OpenIdCodeResource openIdCodeResource;
    private OpenIdImplicitResource openIdImplicitResource;
    private OpenIdImplicitIdentityResource openIdImplicitIdentityResource;

    @Autowired
    public AuthorizationFactory(OAuth2CodeResource oAuth2CodeResource, OAuth2ImplicitResource oAuth2ImplicitResource, OpenIdCodeResource openIdCodeResource, OpenIdImplicitResource openIdImplicitResource, OpenIdImplicitIdentityResource openIdImplicitIdentityResource) {
        this.oAuth2CodeResource = oAuth2CodeResource;
        this.oAuth2ImplicitResource = oAuth2ImplicitResource;
        this.openIdCodeResource = openIdCodeResource;
        this.openIdImplicitResource = openIdImplicitResource;
        this.openIdImplicitIdentityResource = openIdImplicitIdentityResource;
    }

    public Resource<TokenSession, WebSiteUser> makeResource(List<String> scopes, List<String> responseTypes) {

        List<String> scopesSplit = splitInput(scopes);
        List<String> responseTypesSplit = splitInput(responseTypes);

        boolean isOpenId = isOpenId(scopesSplit);

        Resource<TokenSession, WebSiteUser> resource = oAuth2CodeResource;

        if (isOpenIdCodeGrant(isOpenId, responseTypesSplit)) {
            resource = openIdCodeResource;
        } else if (isOpenIdImplicitGrant(isOpenId, responseTypesSplit)) {
            resource = openIdImplicitResource;
        } else if (isOpenIdImplicitIdentityGrant(isOpenId, responseTypesSplit)){
            resource = openIdImplicitIdentityResource;
        } else if (isOpenId) {
            // default to a open id grant flow if open id
            resource = openIdCodeResource;
        }else if (isOAuthCodeGrant(isOpenId, responseTypesSplit)) {
            resource = oAuth2CodeResource;
        } else if (isOAuthImplicitGrant(isOpenId, responseTypesSplit)) {
            resource = oAuth2ImplicitResource;
        }
        return resource;
    }

    protected List<String> splitInput(List<String> input) {
        List<String> output = new ArrayList<>();
        if (input != null && input.size() > 0 && input.get(0) != null) {
            output = Arrays.asList(input.get(0).split(" "))
                    .stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
        }
        return output;
    }

    /**
     * Determines if scopes has the item, openid.
     *
     * The value of the scope parameter is expressed as a list of space-
     * delimited, case-sensitive strings
     * https://tools.ietf.org/html/rfc6749#section-3.3
     *
     * @param scopes
     * @return
     */
    protected boolean isOpenId(List<String> scopes) {
        return scopes.contains(OPENID_SCOPE);
    }

    protected boolean isOpenIdCodeGrant(boolean isOpenId, List<String> responseTypes) {
        return (isOpenId && responseTypes != null && responseTypes.size() == 1 && AUTHORIZATION_CODE_GRANT.equals(responseTypes.get(0).toLowerCase()));
    }

    protected boolean isOpenIdImplicitGrant(boolean isOpenId, List<String> responseTypes) {

        boolean hasImplicitAndIdentity = responseTypes.size() == 2 &&
                (responseTypes.contains(IMPLICIT_GRANT) && responseTypes.contains(IDENTITY_RESPONSE_TYPE));

        return (isOpenId && hasImplicitAndIdentity);
    }

    protected boolean isOpenIdImplicitIdentityGrant(boolean isOpenId, List<String> responseTypes) {

        boolean hasIdentityOnly = responseTypes.size() == 1 && responseTypes.contains(IDENTITY_RESPONSE_TYPE);

        return (isOpenId && hasIdentityOnly);
    }

    protected boolean isOAuthCodeGrant(boolean isOpenId, List<String> responseTypes) {
        return (!isOpenId && responseTypes != null && responseTypes.size() == 1 && AUTHORIZATION_CODE_GRANT.equals(responseTypes.get(0).toLowerCase()));
    }

    protected boolean isOAuthImplicitGrant(boolean isOpenId, List<String> responseTypes) {
        return (!isOpenId && responseTypes != null && responseTypes.size() == 1 && IMPLICIT_GRANT.equals(responseTypes.get(0).toLowerCase()));
    }
}
