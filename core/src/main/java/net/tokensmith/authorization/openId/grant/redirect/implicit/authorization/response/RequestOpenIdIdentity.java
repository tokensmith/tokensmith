package net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.response;

import net.tokensmith.authorization.authenticate.CreateLocalToken;
import net.tokensmith.authorization.authenticate.LoginResourceOwner;
import net.tokensmith.authorization.authenticate.exception.LocalSessionException;
import net.tokensmith.authorization.authenticate.exception.UnauthorizedException;
import net.tokensmith.authorization.authenticate.model.Session;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.exception.ServerException;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenClaims;
import net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.request.ValidateOpenIdIdImplicitGrant;
import net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.request.entity.OpenIdImplicitAuthRequest;
import net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.response.entity.OpenIdImplicitIdentity;
import net.tokensmith.authorization.openId.identity.MakeImplicitIdentityToken;
import net.tokensmith.authorization.openId.identity.exception.IdTokenException;
import net.tokensmith.authorization.openId.identity.exception.KeyNotFoundException;
import net.tokensmith.authorization.openId.identity.exception.ProfileNotFoundException;
import net.tokensmith.repository.entity.ResourceOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by tommackenzie on 9/8/16.
 */
@Component
public class RequestOpenIdIdentity {
    private ValidateOpenIdIdImplicitGrant validateOpenIdIdImplicitGrant;
    private LoginResourceOwner loginResourceOwner;
    private MakeImplicitIdentityToken makeImplicitIdentityToken;
    private CreateLocalToken createLocalToken;

    private String issuer;
    private static String EXCEPTION_MESSAGE = "Failed to create id_token";
    private static String SERVER_ERROR = "server_error";
    private static final Long SECONDS_TO_EXPIRATION = 3600L;

    @Autowired
    public RequestOpenIdIdentity(ValidateOpenIdIdImplicitGrant validateOpenIdIdImplicitGrant, LoginResourceOwner loginResourceOwner, MakeImplicitIdentityToken makeImplicitIdentityToken, CreateLocalToken createLocalToken, String issuer) {
        this.validateOpenIdIdImplicitGrant = validateOpenIdIdImplicitGrant;
        this.loginResourceOwner = loginResourceOwner;
        this.makeImplicitIdentityToken = makeImplicitIdentityToken;
        this.createLocalToken = createLocalToken;
        this.issuer = issuer;
    }

    public OpenIdImplicitIdentity request(String username, String password, Map<String, List<String>> parameters) throws InformResourceOwnerException, InformClientException, UnauthorizedException, ServerException {
        OpenIdImplicitAuthRequest request = validateOpenIdIdImplicitGrant.run(parameters);

        ResourceOwner resourceOwner = loginResourceOwner.run(username, password);

        List<String> audience = new ArrayList<>();
        audience.add(request.getClientId().toString());

        TokenClaims tc = new TokenClaims();
        tc.setIssuer(issuer);
        tc.setAudience(audience);
        tc.setIssuedAt(OffsetDateTime.now().toEpochSecond());
        tc.setAuthTime(OffsetDateTime.now().toEpochSecond());
        tc.setExpirationTime(OffsetDateTime.now().plusSeconds(SECONDS_TO_EXPIRATION).toEpochSecond());

        String idToken;
        try {
            // does not fetch scopes from db since they were validated with, validateOpenIdIdImplicitGrant
            idToken = makeImplicitIdentityToken.makeIdentityOnly(
                request.getNonce(), tc, resourceOwner, request.getScopes()
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


        Session localSession = null;
        try {
            localSession = createLocalToken.makeAndRevokeSession(resourceOwner.getId(), 1);
        } catch (LocalSessionException e) {
            ErrorCode ec = ErrorCode.SERVER_ERROR;
            throw buildInformClientException(ec, request.getRedirectURI(), request.getState(), e);
        }

        OpenIdImplicitIdentity response =  new OpenIdImplicitIdentity();
        response.setIdToken(idToken);
        response.setRedirectUri(request.getRedirectURI());
        response.setState(request.getState());
        response.setScope(Optional.empty());
        response.setSessionToken(localSession.getToken());
        response.setSessionTokenIssuedAt(localSession.getIssuedAt());

        return response;
    }

    protected InformClientException buildInformClientException(ErrorCode ec, URI redirectURI, Optional<String> state, Throwable cause) {
        return new InformClientException(
                EXCEPTION_MESSAGE, SERVER_ERROR, ec.getDescription(), ec.getCode(), redirectURI, state, cause
        );
    }
}
