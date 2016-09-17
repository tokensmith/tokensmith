package org.rootservices.authorization.oauth2.grant.redirect.code.token;

import org.rootservices.authorization.authenticate.LoginConfidentialClient;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.exception.AuthorizationCodeNotFound;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.exception.BadRequestException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.exception.CompromisedCodeException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.exception.BadRequestExceptionBuilder;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.factory.JsonToTokenRequest;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.factory.exception.DuplicateKeyException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.factory.exception.UnknownKeyException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.request.TokenInput;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.response.Extension;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.response.TokenResponse;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.response.TokenType;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.validator.exception.GrantTypeInvalidException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.validator.exception.InvalidValueException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.validator.exception.MissingKeyException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.factory.exception.InvalidPayloadException;
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
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 */
@Component
public class RequestTokenCodeGrant implements RequestToken {
    private LoginConfidentialClient loginConfidentialClient;
    private JsonToTokenRequest jsonToTokenRequest;
    private BadRequestExceptionBuilder badRequestExceptionBuilder;
    private HashTextStaticSalt hashText;
    private AuthCodeRepository authCodeRepository;
    private RandomString randomString;
    private IssueTokenCodeGrant issueTokenCodeGrant;

    @Autowired
    public RequestTokenCodeGrant(LoginConfidentialClient loginConfidentialClient, JsonToTokenRequest jsonToTokenRequest, BadRequestExceptionBuilder badRequestExceptionBuilder, HashTextStaticSalt hashText, AuthCodeRepository authCodeRepository, RandomString randomString, IssueTokenCodeGrant issueTokenCodeGrant) {
        this.loginConfidentialClient = loginConfidentialClient;
        this.jsonToTokenRequest = jsonToTokenRequest;
        this.badRequestExceptionBuilder = badRequestExceptionBuilder;
        this.hashText = hashText;
        this.authCodeRepository = authCodeRepository;
        this.randomString = randomString;
        this.issueTokenCodeGrant = issueTokenCodeGrant;
    }

    @Override
    public TokenResponse run(TokenInput tokenInput) throws UnauthorizedException, AuthorizationCodeNotFound, BadRequestException, CompromisedCodeException {

        // login in a confidential client.
        UUID clientUUID = UUID.fromString(tokenInput.getClientUUID());
        ConfidentialClient confidentialClient = loginConfidentialClient.run(clientUUID, tokenInput.getClientPassword());

        // parse input to a pojo
        TokenRequest tokenRequest = payloadToTokenRequest(tokenInput.getPayload());

        // fetch auth code
        String hashedCode = hashText.run(tokenRequest.getCode());
        AuthCode authCode = fetchAndVerifyAuthCode(clientUUID, hashedCode, tokenRequest.getRedirectUri());

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

    protected Boolean isOpenId(List<AccessRequestScope> accessRequestScopes) {
        for(AccessRequestScope ars: accessRequestScopes) {
            if (ars.getScope().getName().equalsIgnoreCase("openid")) {
                return true;
            }
        }
        return false;
    }
}
