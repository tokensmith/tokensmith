package net.tokensmith.authorization.oauth2.grant.redirect.code.token;

import net.tokensmith.authorization.authenticate.LoginConfidentialClient;
import net.tokensmith.authorization.authenticate.exception.UnauthorizedException;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.exception.BadRequestException;
import net.tokensmith.authorization.exception.ServerException;
import net.tokensmith.authorization.oauth2.grant.redirect.code.token.entity.TokenInputCodeGrant;
import net.tokensmith.authorization.oauth2.grant.redirect.code.token.exception.CompromisedCodeException;
import net.tokensmith.authorization.oauth2.grant.redirect.code.token.factory.TokenInputCodeGrantFactory;
import net.tokensmith.authorization.oauth2.grant.token.RequestTokenGrant;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenResponse;
import net.tokensmith.authorization.oauth2.grant.token.exception.BadRequestExceptionBuilder;
import net.tokensmith.authorization.oauth2.grant.token.exception.InvalidValueException;
import net.tokensmith.authorization.oauth2.grant.token.exception.MissingKeyException;
import net.tokensmith.authorization.oauth2.grant.token.exception.NotFoundException;
import net.tokensmith.authorization.oauth2.grant.token.exception.UnknownKeyException;
import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.repository.entity.AuthCode;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ConfidentialClient;
import net.tokensmith.repository.entity.Scope;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.AuthCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by tommackenzie on 5/24/15.
 */
@Component
public class RequestTokenCodeGrant implements RequestTokenGrant {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestTokenCodeGrant.class);
    private LoginConfidentialClient loginConfidentialClient;
    private TokenInputCodeGrantFactory tokenInputCodeGrantFactory;
    private HashToken hashToken;
    private AuthCodeRepository authCodeRepository;
    private IssueTokenCodeGrant issueTokenCodeGrant;

    @Autowired
    public RequestTokenCodeGrant(LoginConfidentialClient loginConfidentialClient, TokenInputCodeGrantFactory tokenInputCodeGrantFactory, HashToken hashToken, AuthCodeRepository authCodeRepository, IssueTokenCodeGrant issueTokenCodeGrant) {
        this.loginConfidentialClient = loginConfidentialClient;
        this.tokenInputCodeGrantFactory = tokenInputCodeGrantFactory;
        this.hashToken = hashToken;
        this.authCodeRepository = authCodeRepository;
        this.issueTokenCodeGrant = issueTokenCodeGrant;
    }

    public TokenResponse request(UUID clientId, String clientPassword, Map<String, String> request) throws UnauthorizedException, NotFoundException, BadRequestException, ServerException {

        // login in a confidential client.
        ConfidentialClient cc = loginConfidentialClient.run(clientId, clientPassword);

        TokenInputCodeGrant input;
        try {
            input = tokenInputCodeGrantFactory.run(request);
        } catch (UnknownKeyException e) {
            throw new BadRequestExceptionBuilder().UnknownKey(e.getKey(), e.getCode(), e).build();
        } catch (InvalidValueException e) {
            throw new BadRequestExceptionBuilder().InvalidKeyValue(e.getKey(), e.getCode(), e).build();
        } catch (MissingKeyException e) {
            throw new BadRequestExceptionBuilder().MissingKey(e.getKey(), e).build();
        }

        // fetch auth code
        String hashedCode = hashToken.run(input.getCode());
        AuthCode authCode = fetchAndVerifyAuthCode(clientId, hashedCode, input.getRedirectUri());

        UUID resourceOwnerId = authCode.getAccessRequest().getResourceOwnerId();
        List<Scope> scopes = authCode.getAccessRequest().getAccessRequestScopes().stream()
                .map(i -> i.getScope()).collect(Collectors.toList());

        List<Client> audience = new ArrayList<>();
        audience.add(cc.getClient());

        TokenResponse tokenResponse;
        try {
            tokenResponse = issueTokenCodeGrant.run(
                    cc.getClient().getId(),
                    authCode.getId(),
                    resourceOwnerId,
                    scopes,
                    audience,
                    authCode.getAccessRequest().getNonce()
            );
        } catch (CompromisedCodeException e) {
            throw new BadRequestExceptionBuilder().CompromisedCode(e.getCode(), e).build();
        } catch (ServerException e) {
            throw e;
        }

        return tokenResponse;
    }

    protected AuthCode fetchAndVerifyAuthCode(UUID clientUUID, String hashedCode, Optional<URI> tokenRequestRedirectUri) throws NotFoundException {
        AuthCode authCode;
        try {
            authCode = authCodeRepository.getByClientIdAndAuthCode(clientUUID, hashedCode);
        } catch (RecordNotFoundException e) {
            // TODO: security - could a client be phishing for other client's auth codes?
            LOGGER.debug(e.getMessage(), e);
            throw new NotFoundException(
                    "Access Request was not found",
                    "invalid_grant",
                    ErrorCode.AUTH_CODE_NOT_FOUND.getDescription(),
                    ErrorCode.AUTH_CODE_NOT_FOUND.getCode(),
                    e
            );
        }

        if ( ! doRedirectUrisMatch(tokenRequestRedirectUri, authCode.getAccessRequest().getRedirectURI()) ) {
            LOGGER.debug("mismatch: tr: " + tokenRequestRedirectUri + " ar: " + authCode.getAccessRequest().getRedirectURI());
            throw new NotFoundException(
                    "Access Request was not found",
                    "invalid_grant",
                    ErrorCode.REDIRECT_URI_MISMATCH.getDescription(),
                    ErrorCode.REDIRECT_URI_MISMATCH.getCode(),
                    null
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
}
