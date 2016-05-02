package org.rootservices.authorization.grant.code.protocol.token;

import org.rootservices.authorization.authenticate.LoginConfidentialClient;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.token.exception.AuthorizationCodeNotFound;
import org.rootservices.authorization.grant.code.protocol.token.exception.BadRequestException;
import org.rootservices.authorization.grant.code.protocol.token.exception.CompromisedCodeException;
import org.rootservices.authorization.grant.code.protocol.token.exception.BadRequestExceptionBuilder;
import org.rootservices.authorization.grant.code.protocol.token.factory.JsonToTokenRequest;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.*;
import org.rootservices.authorization.grant.code.protocol.token.request.TokenInput;
import org.rootservices.authorization.grant.code.protocol.token.response.Extension;
import org.rootservices.authorization.grant.code.protocol.token.response.TokenResponse;
import org.rootservices.authorization.grant.code.protocol.token.response.TokenType;
import org.rootservices.authorization.grant.code.protocol.token.validator.exception.GrantTypeInvalidException;
import org.rootservices.authorization.grant.code.protocol.token.validator.exception.InvalidValueException;
import org.rootservices.authorization.grant.code.protocol.token.validator.exception.MissingKeyException;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.*;
import org.rootservices.authorization.security.HashTextStaticSalt;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 */
@Component
public class RequestTokenImpl implements RequestToken {
    private LoginConfidentialClient loginConfidentialClient;
    private JsonToTokenRequest jsonToTokenRequest;
    private BadRequestExceptionBuilder badRequestExceptionBuilder;
    private HashTextStaticSalt hashText;
    private AuthCodeRepository authCodeRepository;
    private RandomString randomString;
    private MakeToken makeToken;
    private TokenRepository tokenRepository;
    private AuthCodeTokenRepository authCodeTokenRepository;
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;
    private TokenScopeRepository tokenScopeRepository;

    @Autowired
    public RequestTokenImpl(LoginConfidentialClient loginConfidentialClient, JsonToTokenRequest jsonToTokenRequest, BadRequestExceptionBuilder badRequestExceptionBuilder, HashTextStaticSalt hashText, AuthCodeRepository authCodeRepository, RandomString randomString, MakeToken makeToken, TokenRepository tokenRepository, AuthCodeTokenRepository authCodeTokenRepository, ResourceOwnerTokenRepository resourceOwnerTokenRepository, TokenScopeRepository tokenScopeRepository) {
        this.loginConfidentialClient = loginConfidentialClient;
        this.jsonToTokenRequest = jsonToTokenRequest;
        this.badRequestExceptionBuilder = badRequestExceptionBuilder;
        this.hashText = hashText;
        this.authCodeRepository = authCodeRepository;
        this.randomString = randomString;
        this.makeToken = makeToken;
        this.tokenRepository = tokenRepository;
        this.authCodeTokenRepository = authCodeTokenRepository;
        this.resourceOwnerTokenRepository = resourceOwnerTokenRepository;
        this.tokenScopeRepository = tokenScopeRepository;
    }

    /**
     * Login a confidential client
     * Make token request
     * Validate token request
     * Fetches authorization code.
     *
     * @param tokenInput Request object with data that is needed to make a token
     * @return TokenResponse A response object that resembles a OAuth2 token response
     * @throws UnauthorizedException The client was not able to authenticate
     * @throws AuthorizationCodeNotFound Could not make the token because the authorization could not be found (may have expired, may not exist, maybe revoked)
     * @throws BadRequestException The tokenInput.payload could not be translated to a, TokenRequest
     * @throws CompromisedCodeException The authorization code has already been used to generate a token.
     */
    @Override
    public TokenResponse run(TokenInput tokenInput) throws UnauthorizedException, AuthorizationCodeNotFound, BadRequestException, CompromisedCodeException {

        UUID clientUUID = UUID.fromString(tokenInput.getClientUUID());
        ConfidentialClient confidentialClient = loginConfidentialClient.run(clientUUID, tokenInput.getClientPassword());

        TokenRequest tokenRequest = payloadToTokenRequest(tokenInput.getPayload());

        // once more grant types are implemented then code below will moved to its own class.
        String hashedCode = hashText.run(tokenRequest.getCode());
        AuthCode authCode = fetchAndVerifyAuthCode(clientUUID, hashedCode, tokenRequest.getRedirectUri());

        String plainTextToken = randomString.run();
        UUID resourceOwnerId = authCode.getAccessRequest().getResourceOwnerUUID();
        List<AccessRequestScope> accessRequestScopes = authCode.getAccessRequest().getAccessRequestScopes();

        Token token = grantToken(authCode.getUuid(), resourceOwnerId, plainTextToken, accessRequestScopes);
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(plainTextToken);
        tokenResponse.setExpiresIn(makeToken.getSecondsToExpiration());
        tokenResponse.setTokenType(TokenType.BEARER);

        Extension extension = Extension.NONE;
        if (isOpenId(authCode.getAccessRequest().getAccessRequestScopes())) {
            extension = Extension.IDENTITY;
        }
        tokenResponse.setExtension(extension);

        return tokenResponse;
    }

    /**
     * Makes a token request and validates the token request is accurate.
     *
     * @param payload
     * @return A object that represent a request for a token
     * @throws BadRequestException A exception that contains information on why it was a bad request.
     */
    protected TokenRequest payloadToTokenRequest(BufferedReader payload) throws BadRequestException {

        TokenRequest tokenRequest = null;
        try {
            tokenRequest = jsonToTokenRequest.run(payload);
        } catch (DuplicateKeyException e) {
            throw badRequestExceptionBuilder.DuplicateKey(e.getKey(), e.getCode(), e).build();
        } catch (InvalidPayloadException e) {
            throw badRequestExceptionBuilder.InvalidPayload(e.getCode(), e).build();
        } catch (GrantTypeInvalidException e) {
            throw badRequestExceptionBuilder.UnsupportedGrantType(e.getValue(), e.getCode(), e).build();
        } catch (InvalidValueException e) {
            throw badRequestExceptionBuilder.InvalidKeyValue(e.getKey(), e.getCode(), e).build();
        } catch (MissingKeyException e) {
            throw badRequestExceptionBuilder.MissingKey(e.getKey(), e).build();
        } catch (UnknownKeyException e) {
            throw badRequestExceptionBuilder.UnknownKey(e.getKey(), e.getCode(), e).build();
        }
        return tokenRequest;
    }

    protected AuthCode fetchAndVerifyAuthCode(UUID clientUUID, String hashedCode, Optional<URI> tokenRequestRedirectUri) throws AuthorizationCodeNotFound {
        AuthCode authCode;
        try {
            authCode = authCodeRepository.getByClientIdAndAuthCode(clientUUID, hashedCode);
        } catch (RecordNotFoundException e) {
            throw new AuthorizationCodeNotFound(
                    "Access Request was not found", "invalid_grant", e, ErrorCode.AUTH_CODE_NOT_FOUND.getCode()
            );
        }

        if ( ! doRedirectUrisMatch(tokenRequestRedirectUri, authCode.getAccessRequest().getRedirectURI()) ) {
            throw new AuthorizationCodeNotFound(
                    "Access Request was not found", "invalid_grant", ErrorCode.REDIRECT_URI_MISMATCH.getCode()
            );
        }
        return authCode;
    }

    protected Boolean doRedirectUrisMatch(Optional<URI> redirectUriA, Optional<URI> redirectUriB) {
        Boolean matches = true;
        if ( redirectUriA.isPresent() && ! redirectUriB.isPresent()) {
            matches = false;
        } else if ( !redirectUriA.isPresent() && redirectUriB.isPresent()) {
            matches = false;
        } else if ( redirectUriA.get().equals(redirectUriB.get())) {
            matches = true;
        }
        return matches;
    }

    protected Token grantToken(UUID authCodeId, UUID resourceOwnerId, String plainTextToken, List<AccessRequestScope> accessRequestScopes) throws CompromisedCodeException {
        Token token = makeToken.run(plainTextToken);

        try {
            tokenRepository.insert(token);

            AuthCodeToken authCodeToken = new AuthCodeToken();
            authCodeToken.setId(UUID.randomUUID());
            authCodeToken.setTokenId(token.getUuid());
            authCodeToken.setAuthCodeId(authCodeId);

            authCodeTokenRepository.insert(authCodeToken);

            ResourceOwner resourceOwner = new ResourceOwner();
            resourceOwner.setUuid(resourceOwnerId);

            ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();
            resourceOwnerToken.setId(UUID.randomUUID());
            resourceOwnerToken.setResourceOwner(resourceOwner);
            resourceOwnerToken.setToken(token);

            resourceOwnerTokenRepository.insert(resourceOwnerToken);

            for(AccessRequestScope ars: accessRequestScopes) {
                TokenScope ts = new TokenScope();
                ts.setId(UUID.randomUUID());
                ts.setTokenId(token.getUuid());
                ts.setScope(ars.getScope());

                tokenScopeRepository.insert(ts);
            }

        } catch (DuplicateRecordException e) {
            tokenRepository.revokeByAuthCodeId(authCodeId);
            authCodeRepository.revokeById(authCodeId);

            throw new CompromisedCodeException(
                    ErrorCode.COMPROMISED_AUTH_CODE.getMessage(),
                    "invalid_grant", e, ErrorCode.COMPROMISED_AUTH_CODE.getCode()
            );
        }
        return token;
    }

    protected Boolean isOpenId(List<AccessRequestScope> accessRequestScopes) {
        for(AccessRequestScope ars: accessRequestScopes) {
            if (ars.getScope().getName().equalsIgnoreCase("openid")) {
                return true;
            }
        }
        return false;
    }
}
