package net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import net.tokensmith.authorization.authenticate.LoginResourceOwner;
import net.tokensmith.authorization.authenticate.exception.UnauthorizedException;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.exception.ServerException;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.builder.InformClientExceptionBuilder;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenClaims;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenGraph;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenType;
import net.tokensmith.authorization.oauth2.grant.redirect.implicit.authorization.response.IssueTokenImplicitGrant;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.request.ValidateOpenIdIdImplicitGrant;
import net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.request.entity.OpenIdImplicitAuthRequest;
import net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.response.builder.OpenIdImplicitAccessTokenBuilder;
import net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.response.entity.OpenIdImplicitAccessToken;
import net.tokensmith.authorization.openId.identity.MakeImplicitIdentityToken;
import net.tokensmith.authorization.openId.identity.exception.IdTokenException;
import net.tokensmith.authorization.openId.identity.exception.KeyNotFoundException;
import net.tokensmith.authorization.openId.identity.exception.ProfileNotFoundException;
import net.tokensmith.repository.entity.*;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tommackenzie on 8/30/16.
 */
@Component
public class RequestOpenIdImplicitTokenAndIdentity {
    private static final Logger logger = LogManager.getLogger(RequestOpenIdImplicitTokenAndIdentity.class);

    private ValidateOpenIdIdImplicitGrant validateOpenIdIdImplicitGrant;
    private LoginResourceOwner loginResourceOwner;
    private IssueTokenImplicitGrant issueTokenImplicitGrant;
    private MakeImplicitIdentityToken makeImplicitIdentityToken;
    private OpenIdImplicitAccessTokenBuilder openIdImplicitAccessTokenBuilder;
    private ClientRepository clientRepository;

    private String issuer;
    private static String MSG_ID_TOKEN = "Failed to create id_token";
    private static String MSG_TOKEN = "Failed to issue token";
    private static String SERVER_ERROR = "server_error";

    @Autowired
    public RequestOpenIdImplicitTokenAndIdentity(ValidateOpenIdIdImplicitGrant validateOpenIdIdImplicitGrant, LoginResourceOwner loginResourceOwner, IssueTokenImplicitGrant issueTokenImplicitGrant, MakeImplicitIdentityToken makeImplicitIdentityToken, OpenIdImplicitAccessTokenBuilder openIdImplicitAccessTokenBuilder, ClientRepository clientRepository, String issuer) {
        this.validateOpenIdIdImplicitGrant = validateOpenIdIdImplicitGrant;
        this.loginResourceOwner = loginResourceOwner;
        this.issueTokenImplicitGrant = issueTokenImplicitGrant;
        this.makeImplicitIdentityToken = makeImplicitIdentityToken;
        this.openIdImplicitAccessTokenBuilder = openIdImplicitAccessTokenBuilder;
        this.clientRepository = clientRepository;
        this.issuer = issuer;
    }

    public OpenIdImplicitAccessToken request(String username, String password, Map<String, List<String>> parameters) throws InformResourceOwnerException, InformClientException, UnauthorizedException, ServerException {
        OpenIdImplicitAuthRequest request = validateOpenIdIdImplicitGrant.run(parameters);

        ResourceOwner resourceOwner = loginResourceOwner.run(username, password);
        List<Client> audience = makeAudience(request.getClientId());

        TokenGraph tokenGraph;
        try {
            tokenGraph = issueTokenImplicitGrant.run(request.getClientId(), resourceOwner, request.getScopes(), audience);
        } catch (ServerException e) {
            logger.error(e.getMessage(), e);

            ErrorCode ec = ErrorCode.SERVER_ERROR;
            throw new InformClientExceptionBuilder()
                    .setMessage(MSG_TOKEN)
                    .setError(SERVER_ERROR)
                    .setDescription(ec.getDescription())
                    .setErrorCode(ec.getCode())
                    .setRedirectURI(request.getRedirectURI())
                    .setState(request.getState())
                    .setCause(e)
                    .build();
        }

        List<String> scopesForIdToken = tokenGraph.getToken().getTokenScopes().stream()
                .map(item -> item.getScope().getName())
                .collect(Collectors.toList());

        List<String> audienceForClaim = tokenGraph.getToken().getAudience()
                .stream()
                .map(i->i.getId().toString())
                .collect(Collectors.toList());

        TokenClaims tc = new TokenClaims();
        tc.setIssuer(issuer);
        tc.setAudience(audienceForClaim);
        tc.setIssuedAt(tokenGraph.getToken().getCreatedAt().toEpochSecond());
        tc.setExpirationTime(tokenGraph.getToken().getExpiresAt().toEpochSecond());
        tc.setAuthTime(tokenGraph.getToken().getCreatedAt().toEpochSecond());

        String idToken;
        try {
            idToken = makeImplicitIdentityToken.makeForAccessToken(
                tokenGraph.getPlainTextAccessToken(), request.getNonce(), tc, resourceOwner, scopesForIdToken
            );
        } catch (ProfileNotFoundException e) {
            logger.error(e.getMessage(), e);
            ErrorCode ec = ErrorCode.PROFILE_NOT_FOUND;
            throw buildInformClientException(ec, request.getRedirectURI(), request.getState(), e);
        } catch (KeyNotFoundException e) {
            logger.error(e.getMessage(), e);
            ErrorCode ec = ErrorCode.SIGN_KEY_NOT_FOUND;
            throw buildInformClientException(ec, request.getRedirectURI(), request.getState(), e);
        } catch (IdTokenException e) {
            logger.error(e.getMessage(), e);
            ErrorCode ec = ErrorCode.JWT_ENCODING_ERROR;
            throw buildInformClientException(ec, request.getRedirectURI(), request.getState(), e);
        }

        OpenIdImplicitAccessToken response = openIdImplicitAccessTokenBuilder
            .setAccessToken(tokenGraph.getPlainTextAccessToken())
            .setExpiresIn(tokenGraph.getToken().getSecondsToExpiration())
            .setIdToken(idToken)
            .setRedirectUri(request.getRedirectURI())
            .setState(request.getState())
            .setScope(Optional.empty())
            .setTokenType(TokenType.BEARER)
            .build();

        return response;
    }

    protected InformClientException buildInformClientException(ErrorCode ec, URI redirectURI, Optional<String> state, Throwable cause) {
        return new InformClientExceptionBuilder()
            .setMessage(MSG_ID_TOKEN)
            .setError(SERVER_ERROR)
            .setDescription(ec.getDescription())
            .setErrorCode(ec.getCode())
            .setRedirectURI(redirectURI)
            .setState(state)
            .setCause(cause)
            .build();
    }

    private List<Client> makeAudience(UUID clientId) throws InformResourceOwnerException {
        List<Client> audience = new ArrayList<>();

        Client client;
        try {
            client = clientRepository.getById(clientId);
        } catch (RecordNotFoundException e) {
            throw new InformResourceOwnerException(
                    ErrorCode.CLIENT_NOT_FOUND.getDescription(), e, ErrorCode.CLIENT_NOT_FOUND.getCode()
            );
        }
        audience.add(client);
        return audience;
    }
}
