package org.rootservices.authorization.grant.code.protocol.token;

import org.rootservices.authorization.authenticate.LoginConfidentialClient;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.exception.BaseInformException;
import org.rootservices.authorization.grant.code.constant.ErrorCode;
import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.AccessRequestRepository;
import org.rootservices.authorization.persistence.repository.TokenRepository;
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
    private AccessRequestRepository accessRequestRepository;
    private RandomString randomString;
    private MakeToken makeToken;
    private TokenRepository tokenRepository;

    @Autowired
    public RequestTokenImpl(LoginConfidentialClient loginConfidentialClient, AccessRequestRepository accessRequestRepository, RandomString randomString, MakeToken makeToken, TokenRepository tokenRepository) {
        this.loginConfidentialClient = loginConfidentialClient;
        this.accessRequestRepository = accessRequestRepository;
        this.randomString = randomString;
        this.makeToken = makeToken;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public TokenResponse run(UUID clientUUID, String clientPassword, String code, Optional<URI> redirectURI) throws UnauthorizedException, BaseInformException {

        ConfidentialClient confidentialClient = loginConfidentialClient.run(clientUUID, clientPassword);

        AccessRequest accessRequest;
        try {
            accessRequest = accessRequestRepository.getByClientUUIDAndAuthCode(clientUUID, code);
        } catch (RecordNotFoundException e) {
            throw new BaseInformException("Access Request was not found", e, ErrorCode.ACCESS_REQUEST_NOT_FOUND.getCode());
        }

        String plainTextToken = randomString.run();
        Token token = makeToken.run(accessRequest.getAuthCodeUUID(), plainTextToken);
        tokenRepository.insert(token);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(plainTextToken);
        tokenResponse.setSecondsToExpiration(makeToken.getSecondsToExpiration());
        tokenResponse.setTokenType(makeToken.getTokenType());
        return tokenResponse;
    }
}
