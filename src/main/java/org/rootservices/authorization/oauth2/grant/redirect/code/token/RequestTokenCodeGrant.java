package org.rootservices.authorization.oauth2.grant.redirect.code.token;

import org.rootservices.authorization.authenticate.LoginConfidentialClient;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.entity.TokenInputCodeGrant;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.exception.AuthorizationCodeNotFound;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.factory.TokenInputCodeGrantFactory;
import org.rootservices.authorization.oauth2.grant.token.exception.BadRequestException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.exception.CompromisedCodeException;
import org.rootservices.authorization.oauth2.grant.token.exception.BadRequestExceptionBuilder;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.factory.JsonToTokenRequest;
import org.rootservices.authorization.oauth2.grant.token.exception.DuplicateKeyException;
import org.rootservices.authorization.oauth2.grant.token.exception.UnknownKeyException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.request.TokenInput;
import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.validator.exception.GrantTypeInvalidException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.validator.exception.InvalidValueException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.validator.exception.MissingKeyException;
import org.rootservices.authorization.oauth2.grant.token.exception.InvalidPayloadException;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.*;
import org.rootservices.authorization.security.HashTextStaticSalt;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 */
@Component
public class RequestTokenCodeGrant {
    private LoginConfidentialClient loginConfidentialClient;
    private TokenInputCodeGrantFactory tokenInputCodeGrantFactory;
    private BadRequestExceptionBuilder badRequestExceptionBuilder;
    private HashTextStaticSalt hashText;
    private AuthCodeRepository authCodeRepository;
    private RandomString randomString;
    private IssueTokenCodeGrant issueTokenCodeGrant;

    @Autowired
    public RequestTokenCodeGrant(LoginConfidentialClient loginConfidentialClient, TokenInputCodeGrantFactory tokenInputCodeGrantFactory, JsonToTokenRequest jsonToTokenRequest, BadRequestExceptionBuilder badRequestExceptionBuilder, HashTextStaticSalt hashText, AuthCodeRepository authCodeRepository, RandomString randomString, IssueTokenCodeGrant issueTokenCodeGrant) {
        this.loginConfidentialClient = loginConfidentialClient;
        this.tokenInputCodeGrantFactory = tokenInputCodeGrantFactory;
        this.badRequestExceptionBuilder = badRequestExceptionBuilder;
        this.hashText = hashText;
        this.authCodeRepository = authCodeRepository;
        this.randomString = randomString;
        this.issueTokenCodeGrant = issueTokenCodeGrant;
    }

    public TokenResponse request(UUID clientId, String clientPassword, Map<String, String> request) throws UnauthorizedException, AuthorizationCodeNotFound, BadRequestException, CompromisedCodeException {

        // login in a confidential client.
        ConfidentialClient cc = loginConfidentialClient.run(clientId, clientPassword);

        TokenInputCodeGrant input = null;
        try {
            input = tokenInputCodeGrantFactory.run(request);
        } catch (UnknownKeyException e) {
            throw badRequestExceptionBuilder.UnknownKey(e.getKey(), e.getCode(), e).build();
        } catch (InvalidValueException e) {
            throw badRequestExceptionBuilder.InvalidKeyValue(e.getKey(), e.getCode(), e).build();
        } catch (MissingKeyException e) {
            throw badRequestExceptionBuilder.MissingKey(e.getKey(), e).build();
        }

        // fetch auth code
        String hashedCode = hashText.run(input.getCode());
        AuthCode authCode = fetchAndVerifyAuthCode(clientId, hashedCode, input.getRedirectUri());

        String plainTextToken = randomString.run();
        UUID resourceOwnerId = authCode.getAccessRequest().getResourceOwnerUUID();
        List<AccessRequestScope> accessRequestScopes = authCode.getAccessRequest().getAccessRequestScopes();

        Token token = issueTokenCodeGrant.run(
                authCode.getUuid(),
                resourceOwnerId,
                plainTextToken,
                accessRequestScopes
        );

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(plainTextToken);
        tokenResponse.setExpiresIn(token.getSecondsToExpiration());
        tokenResponse.setTokenType(TokenType.BEARER);

        Extension extension = Extension.NONE;
        if (isOpenId(authCode.getAccessRequest().getAccessRequestScopes())) {
            extension = Extension.IDENTITY;
        }
        tokenResponse.setExtension(extension);

        return tokenResponse;
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

    protected Boolean isOpenId(List<AccessRequestScope> accessRequestScopes) {
        for(AccessRequestScope ars: accessRequestScopes) {
            if (ars.getScope().getName().equalsIgnoreCase("openid")) {
                return true;
            }
        }
        return false;
    }
}
