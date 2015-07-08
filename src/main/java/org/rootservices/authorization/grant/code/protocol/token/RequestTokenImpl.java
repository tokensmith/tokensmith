package org.rootservices.authorization.grant.code.protocol.token;

import org.rootservices.authorization.authenticate.LoginConfidentialClient;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.token.exception.AuthorizationCodeNotFound;
import org.rootservices.authorization.grant.code.protocol.token.exception.BadRequestException;
import org.rootservices.authorization.grant.code.protocol.token.factory.JsonToTokenRequest;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.DuplicateKeyException;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.InvalidPayloadException;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.InvalidValueException;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.MissingKeyException;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.entity.Token;
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
    private HashTextStaticSalt hashText;
    private AuthCodeRepository authCodeRepository;
    private RandomString randomString;
    private MakeToken makeToken;
    private TokenRepository tokenRepository;

    @Autowired
    public RequestTokenImpl(LoginConfidentialClient loginConfidentialClient, JsonToTokenRequest jsonToTokenRequest, HashTextStaticSalt hashText, AuthCodeRepository authCodeRepository, RandomString randomString, MakeToken makeToken, TokenRepository tokenRepository) {
        this.loginConfidentialClient = loginConfidentialClient;
        this.jsonToTokenRequest = jsonToTokenRequest;
        this.hashText = hashText;
        this.authCodeRepository = authCodeRepository;
        this.randomString = randomString;
        this.makeToken = makeToken;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public TokenResponse run(TokenInput tokenInput) throws UnauthorizedException, AuthorizationCodeNotFound, BadRequestException {

        UUID clientUUID = UUID.fromString(tokenInput.getClientUUID());
        ConfidentialClient confidentialClient = loginConfidentialClient.run(clientUUID, tokenInput.getClientPassword());

        TokenRequest tokenRequest = null;
        try {
            tokenRequest = jsonToTokenRequest.run(tokenInput.getPayload());
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
        } catch (InvalidPayloadException e) {
            e.printStackTrace();
        } catch (InvalidValueException e) {
            e.printStackTrace();
        } catch (MissingKeyException e) {
            throw new BadRequestException(
                "Bad request", "invalid_request", e.getKey() + " is a required field", e, ErrorCode.MISSING_KEY.getCode()
            );
        }

        AuthCode authCode = null;
        String hashedCode = hashText.run(tokenRequest.getCode());

        try {
            authCode = authCodeRepository.getByClientUUIDAndAuthCode(clientUUID, hashedCode);
        } catch (RecordNotFoundException e) {
            throw new AuthorizationCodeNotFound("Access Request was not found", e, ErrorCode.ACCESS_REQUEST_NOT_FOUND.getCode());
        }

        if ( ! doRedirectUrisMatch(tokenRequest.getRedirectUri(), authCode.getAccessRequest().getRedirectURI()) ) {
            throw new AuthorizationCodeNotFound("Access Request was not found", ErrorCode.REDIRECT_URI_MISMATCH.getCode());
        }

        String plainTextToken = randomString.run();
        Token token = makeToken.run(authCode.getUuid(), plainTextToken);
        tokenRepository.insert(token);

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
