package org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response;

import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenClaims;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.ValidateOpenIdIdImplicitGrant;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.entity.OpenIdImplicitAuthRequest;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response.entity.OpenIdImplicitIdentity;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.entity.OpenIdInputParams;
import org.rootservices.authorization.openId.identity.MakeImplicitIdentityToken;
import org.rootservices.authorization.openId.identity.exception.IdTokenException;
import org.rootservices.authorization.openId.identity.exception.KeyNotFoundException;
import org.rootservices.authorization.openId.identity.exception.ProfileNotFoundException;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 9/8/16.
 */
@Component
public class RequestOpenIdIdentity {
    private ValidateOpenIdIdImplicitGrant validateOpenIdIdImplicitGrant;
    private LoginResourceOwner loginResourceOwner;
    private MakeImplicitIdentityToken makeImplicitIdentityToken;

    private String issuer;
    private static String EXCEPTION_MESSAGE = "Failed to create id_token";
    private static String SERVER_ERROR = "server_error";
    private static final Long SECONDS_TO_EXPIRATION = 3600L;

    @Autowired
    public RequestOpenIdIdentity(ValidateOpenIdIdImplicitGrant validateOpenIdIdImplicitGrant, LoginResourceOwner loginResourceOwner, MakeImplicitIdentityToken makeImplicitIdentityToken, String issuer) {
        this.validateOpenIdIdImplicitGrant = validateOpenIdIdImplicitGrant;
        this.loginResourceOwner = loginResourceOwner;
        this.makeImplicitIdentityToken = makeImplicitIdentityToken;
        this.issuer = issuer;
    }

    public OpenIdImplicitIdentity request(OpenIdInputParams input) throws InformResourceOwnerException, InformClientException, UnauthorizedException {
        OpenIdImplicitAuthRequest request = validateOpenIdIdImplicitGrant.run(
                input.getClientIds(), input.getResponseTypes(), input.getRedirectUris(), input.getScopes(), input.getStates(), input.getNonces()
        );

        ResourceOwner resourceOwner = loginResourceOwner.run(input.getUserName(), input.getPlainTextPassword());

        // TODO: should it fetch scopes from the database?

        List<String> audience = new ArrayList<>();
        audience.add(request.getClientId().toString());

        TokenClaims tc = new TokenClaims();
        tc.setIssuer(issuer);
        tc.setAudience(audience);
        tc.setIssuedAt(OffsetDateTime.now().toEpochSecond());
        tc.setAuthTime(OffsetDateTime.now().toEpochSecond());
        tc.setExpirationTime(OffsetDateTime.now().plusSeconds(SECONDS_TO_EXPIRATION).toEpochSecond());

        String idToken = null;
        try {
            idToken = makeImplicitIdentityToken.makeIdentityOnly(
                request.getNonce(), tc, resourceOwner.getId(), request.getScopes()
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

        OpenIdImplicitIdentity response =  new OpenIdImplicitIdentity();
        response.setIdToken(idToken);
        response.setRedirectUri(request.getRedirectURI());
        response.setState(request.getState());
        response.setScope(Optional.empty());

        return response;
    }

    protected InformClientException buildInformClientException(ErrorCode ec, URI redirectURI, Optional<String> state, Throwable cause) {
        return new InformClientException(
                EXCEPTION_MESSAGE, SERVER_ERROR, ec.getDescription(), ec.getCode(), redirectURI, state, cause
        );
    }
}
