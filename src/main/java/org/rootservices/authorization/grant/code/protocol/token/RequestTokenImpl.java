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
import org.rootservices.authorization.grant.code.protocol.token.validator.exception.GrantTypeInvalidException;
import org.rootservices.authorization.grant.code.protocol.token.validator.exception.InvalidValueException;
import org.rootservices.authorization.grant.code.protocol.token.validator.exception.MissingKeyException;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.persistence.repository.TokenRepository;
import org.rootservices.authorization.security.HashTextStaticSalt;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
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

    @Autowired
    public RequestTokenImpl(LoginConfidentialClient loginConfidentialClient, JsonToTokenRequest jsonToTokenRequest, BadRequestExceptionBuilder badRequestExceptionBuilder, HashTextStaticSalt hashText, AuthCodeRepository authCodeRepository, RandomString randomString, MakeToken makeToken, TokenRepository tokenRepository) {
        this.loginConfidentialClient = loginConfidentialClient;
        this.jsonToTokenRequest = jsonToTokenRequest;
        this.badRequestExceptionBuilder = badRequestExceptionBuilder;
        this.hashText = hashText;
        this.authCodeRepository = authCodeRepository;
        this.randomString = randomString;
        this.makeToken = makeToken;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public TokenResponse run(TokenInput tokenInput) throws UnauthorizedException, AuthorizationCodeNotFound, BadRequestException, CompromisedCodeException {

        UUID clientUUID = UUID.fromString(tokenInput.getClientUUID());
        ConfidentialClient confidentialClient = loginConfidentialClient.run(clientUUID, tokenInput.getClientPassword());

        TokenRequest tokenRequest = null;
        try {
            tokenRequest = jsonToTokenRequest.run(tokenInput.getPayload());
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

        AuthCode authCode = null;
        String hashedCode = hashText.run(tokenRequest.getCode());

        try {
            authCode = authCodeRepository.getByClientUUIDAndAuthCodeAndNotRevoked(clientUUID, hashedCode);
        } catch (RecordNotFoundException e) {
            throw new AuthorizationCodeNotFound(
                "Access Request was not found", "invalid_grant", e, ErrorCode.AUTH_CODE_NOT_FOUND.getCode()
            );
        }

        if ( ! doRedirectUrisMatch(tokenRequest.getRedirectUri(), authCode.getAccessRequest().getRedirectURI()) ) {
            throw new AuthorizationCodeNotFound(
                "Access Request was not found", "invalid_grant", ErrorCode.REDIRECT_URI_MISMATCH.getCode()
            );
        }

        String plainTextToken = randomString.run();
        Token token = makeToken.run(authCode.getUuid(), plainTextToken);
        try {
            tokenRepository.insert(token);
        } catch (DuplicateRecordException e) {
            tokenRepository.revoke(authCode.getUuid());

            throw new CompromisedCodeException(
                ErrorCode.COMPROMISED_AUTH_CODE.getMessage(),
                "invalid_grant", e, ErrorCode.COMPROMISED_AUTH_CODE.getCode()
            );
        }

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(plainTextToken);
        tokenResponse.setExpiresIn(makeToken.getSecondsToExpiration());
        tokenResponse.setTokenType(makeToken.getTokenType().toString().toLowerCase());
        return tokenResponse;
    }

    private Boolean doRedirectUrisMatch(Optional<URI> redirectUriA, Optional<URI> redirectUriB) {
        Boolean matches = true;
        if ( redirectUriA.isPresent() && ! redirectUriB.isPresent()) {
            matches = false;
        } else if ( !redirectUriA.isPresent() && redirectUriB.isPresent()) {
            matches =false;
        }
        return matches;
    }
}
