package org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response;

import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenClaims;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response.IssueTokenImplicitGrant;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.ValidateOpenIdIdImplicitGrant;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.entity.OpenIdImplicitAuthRequest;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response.builder.OpenIdImplicitAccessTokenBuilder;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response.entity.OpenIdImplicitAccessToken;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.entity.OpenIdInputParams;
import org.rootservices.authorization.openId.identity.MakeImplicitIdentityToken;
import org.rootservices.authorization.openId.identity.exception.IdTokenException;
import org.rootservices.authorization.openId.identity.exception.KeyNotFoundException;
import org.rootservices.authorization.openId.identity.exception.ProfileNotFoundException;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by tommackenzie on 8/30/16.
 */
@Component
public class RequestOpenIdImplicitTokenAndIdentity {
    private ValidateOpenIdIdImplicitGrant validateOpenIdIdImplicitGrant;
    private LoginResourceOwner loginResourceOwner;
    private RandomString randomString;
    private IssueTokenImplicitGrant issueTokenImplicitGrant;
    private MakeImplicitIdentityToken makeImplicitIdentityToken;
    private OpenIdImplicitAccessTokenBuilder openIdImplicitAccessTokenBuilder;

    private String issuer;
    private static String EXCEPTION_MESSAGE = "Failed to create id_token";
    private static String SERVER_ERROR = "server_error";

    @Autowired
    public RequestOpenIdImplicitTokenAndIdentity(ValidateOpenIdIdImplicitGrant validateOpenIdIdImplicitGrant, LoginResourceOwner loginResourceOwner, RandomString randomString, IssueTokenImplicitGrant issueTokenImplicitGrant, MakeImplicitIdentityToken makeImplicitIdentityToken, OpenIdImplicitAccessTokenBuilder openIdImplicitAccessTokenBuilder, String issuer) {
        this.validateOpenIdIdImplicitGrant = validateOpenIdIdImplicitGrant;
        this.loginResourceOwner = loginResourceOwner;
        this.randomString = randomString;
        this.issueTokenImplicitGrant = issueTokenImplicitGrant;
        this.makeImplicitIdentityToken = makeImplicitIdentityToken;
        this.openIdImplicitAccessTokenBuilder = openIdImplicitAccessTokenBuilder;
        this.issuer = issuer;
    }

    public OpenIdImplicitAccessToken request(OpenIdInputParams input) throws InformResourceOwnerException, InformClientException, UnauthorizedException {
        OpenIdImplicitAuthRequest request = validateOpenIdIdImplicitGrant.run(
                input.getClientIds(), input.getResponseTypes(), input.getRedirectUris(), input.getScopes(), input.getStates(), input.getNonces()
        );

        ResourceOwner resourceOwner = loginResourceOwner.run(input.getUserName(), input.getPlainTextPassword());

        String accessToken = randomString.run();
        Token token = issueTokenImplicitGrant.run(request.getClientId(), resourceOwner, request.getScopes(),  accessToken);

        List<String> scopesForIdToken = token.getTokenScopes().stream()
                .map(item -> item.getScope().getName())
                .collect(Collectors.toList());

        List<String> audience = new ArrayList<>();
        audience.add(request.getClientId().toString());

        TokenClaims tc = new TokenClaims();
        tc.setIssuer(issuer);
        tc.setAudience(audience);
        tc.setIssuedAt(token.getCreatedAt().toEpochSecond());
        tc.setExpirationTime(token.getExpiresAt().toEpochSecond());
        tc.setAuthTime(token.getCreatedAt().toEpochSecond());

        String idToken = null;
        try {
            idToken = makeImplicitIdentityToken.makeForAccessToken(
                accessToken, request.getNonce(), tc, resourceOwner.getId(), scopesForIdToken
            );
        } catch (ProfileNotFoundException e) {
            ErrorCode ec = ErrorCode.PROFILE_NOT_FOUND;
            throw buildInformClientException(ec, request.getRedirectURI(), request.getState(), e);
        } catch (KeyNotFoundException e) {
            ErrorCode ec = ErrorCode.SIGN_KEY_NOT_FOUND;
            throw buildInformClientException(ec, request.getRedirectURI(), request.getState(), e);
        } catch (IdTokenException e) {
            ErrorCode ec = ErrorCode.JWT_ENCODING_ERROR;
            throw buildInformClientException(ec, request.getRedirectURI(), request.getState(), e);
        }

        OpenIdImplicitAccessToken response = openIdImplicitAccessTokenBuilder
            .setAccessToken(accessToken)
            .setExpiresIn(token.getSecondsToExpiration())
            .setIdToken(idToken)
            .setRedirectUri(request.getRedirectURI())
            .setState(request.getState())
            .setScope(Optional.empty())
            .setTokenType(TokenType.BEARER)
            .build();

        return response;
    }

    protected InformClientException buildInformClientException(ErrorCode ec, URI redirectURI, Optional<String> state, Throwable cause) {
        return new InformClientException(
            EXCEPTION_MESSAGE, SERVER_ERROR, ec.getDescription(), ec.getCode(), redirectURI, state, cause
        );
    }
}
