package org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response;

import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response.IssueTokenImplicitGrant;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.ValidateOpenIdIdImplicitGrant;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response.entity.OpenIdImplicitAccessToken;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.repository.ScopeRepository;
import org.rootservices.authorization.security.RandomString;

import java.util.List;

/**
 * Created by tommackenzie on 8/30/16.
 */
public class RequestOpenIdImplicitToken {
    private ValidateOpenIdIdImplicitGrant validateOpenIdIdImplicitGrant;
    private LoginResourceOwner loginResourceOwner;
    private ScopeRepository scopeRepository;
    private RandomString randomString;
    private IssueTokenImplicitGrant issueTokenImplicitGrant;

    public OpenIdImplicitAccessToken requestToken(String userName, String password, List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states, List<String> nonces) throws InformResourceOwnerException, InformClientException, UnauthorizedException {
        validateOpenIdIdImplicitGrant.run(
                clientIds, responseTypes, redirectUris, scopes, states, nonces
        );

        // login the user
        ResourceOwner resourceOwner = loginResourceOwner.run(userName, password);

        String accessToken = randomString.run();

        Token token = issueTokenImplicitGrant.run(
                resourceOwner, scopes,  accessToken
        );
        return null;
    }
}
