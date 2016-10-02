package org.rootservices.authorization.oauth2.grant.redirect.code.token;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.exception.CompromisedCodeException;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 8/28/16.
 */
@Component
public class IssueTokenCodeGrant {
    private MakeBearerToken makeBearerToken;
    private TokenRepository tokenRepository;
    private AuthCodeTokenRepository authCodeTokenRepository;
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;
    private TokenScopeRepository tokenScopeRepository;
    private AuthCodeRepository authCodeRepository;
    private ClientTokenRepository clientTokenRepository;

    @Autowired
    public IssueTokenCodeGrant(MakeBearerToken makeBearerToken, TokenRepository tokenRepository, AuthCodeTokenRepository authCodeTokenRepository, ResourceOwnerTokenRepository resourceOwnerTokenRepository, TokenScopeRepository tokenScopeRepository, AuthCodeRepository authCodeRepository, ClientTokenRepository clientTokenRepository) {
        this.makeBearerToken = makeBearerToken;
        this.tokenRepository = tokenRepository;
        this.authCodeTokenRepository = authCodeTokenRepository;
        this.resourceOwnerTokenRepository = resourceOwnerTokenRepository;
        this.tokenScopeRepository = tokenScopeRepository;
        this.authCodeRepository = authCodeRepository;
        this.clientTokenRepository = clientTokenRepository;
    }

    public Token run(UUID clientId, UUID authCodeId, UUID resourceOwnerId, String plainTextToken, List<AccessRequestScope> accessRequestScopes) throws CompromisedCodeException {
        Token token = makeBearerToken.run(plainTextToken);

        try {
            tokenRepository.insert(token);
        } catch( DuplicateRecordException e) {
            // TODO: handle this exception
        }

        try {
            AuthCodeToken authCodeToken = new AuthCodeToken();
            authCodeToken.setId(UUID.randomUUID());
            authCodeToken.setTokenId(token.getId());
            authCodeToken.setAuthCodeId(authCodeId);

            authCodeTokenRepository.insert(authCodeToken);
        } catch (DuplicateRecordException e) {
            tokenRepository.revokeByAuthCodeId(authCodeId);
            authCodeRepository.revokeById(authCodeId);

            throw new CompromisedCodeException(
                    ErrorCode.COMPROMISED_AUTH_CODE.getDescription(),
                    "invalid_grant", e, ErrorCode.COMPROMISED_AUTH_CODE.getCode()
            );
        }

        ResourceOwner resourceOwner = new ResourceOwner();
        resourceOwner.setId(resourceOwnerId);

        ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();
        resourceOwnerToken.setId(UUID.randomUUID());
        resourceOwnerToken.setResourceOwner(resourceOwner);
        resourceOwnerToken.setToken(token);

        resourceOwnerTokenRepository.insert(resourceOwnerToken);

        ClientToken clientToken = new ClientToken();
        clientToken.setId(UUID.randomUUID());
        clientToken.setClientId(clientId);
        clientToken.setTokenId(token.getId());

        clientTokenRepository.insert(clientToken);

        for(AccessRequestScope ars: accessRequestScopes) {
            TokenScope ts = new TokenScope();
            ts.setId(UUID.randomUUID());
            ts.setTokenId(token.getId());
            ts.setScope(ars.getScope());

            tokenScopeRepository.insert(ts);
        }

        return token;
    }
}
