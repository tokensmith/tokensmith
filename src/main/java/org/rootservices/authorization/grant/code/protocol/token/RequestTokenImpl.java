package org.rootservices.authorization.grant.code.protocol.token;

import org.rootservices.authorization.authenticate.LoginConfidentialClient;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.token.exception.AuthorizationCodeNotFound;
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

import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 */
@Component
public class RequestTokenImpl implements RequestToken {
    private LoginConfidentialClient loginConfidentialClient;
    private HashTextStaticSalt hashText;
    private AuthCodeRepository authCodeRepository;
    private RandomString randomString;
    private MakeToken makeToken;
    private TokenRepository tokenRepository;

    @Autowired
    public RequestTokenImpl(LoginConfidentialClient loginConfidentialClient, HashTextStaticSalt hashText, AuthCodeRepository authCodeRepository, RandomString randomString, MakeToken makeToken, TokenRepository tokenRepository) {
        this.loginConfidentialClient = loginConfidentialClient;
        this.hashText = hashText;
        this.authCodeRepository = authCodeRepository;
        this.randomString = randomString;
        this.makeToken = makeToken;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public TokenResponse run(TokenInput tokenInput) throws UnauthorizedException, AuthorizationCodeNotFound {

        UUID clientUUID = UUID.fromString(tokenInput.getClientUUID());
        ConfidentialClient confidentialClient = loginConfidentialClient.run(clientUUID, tokenInput.getClientPassword());

        AuthCode authCode = null;
        String hashedCode = hashText.run(tokenInput.getCode());

        try {
            authCode = authCodeRepository.getByClientUUIDAndAuthCode(clientUUID, hashedCode);
        } catch (RecordNotFoundException e) {
            throw new AuthorizationCodeNotFound("Access Request was not found", e, ErrorCode.ACCESS_REQUEST_NOT_FOUND.getCode());
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
}
