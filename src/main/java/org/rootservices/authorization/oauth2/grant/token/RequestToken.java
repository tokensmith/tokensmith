package org.rootservices.authorization.oauth2.grant.token;

import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.exception.ServerException;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.oauth2.grant.token.exception.*;
import org.rootservices.authorization.oauth2.grant.token.factory.RequestTokenGrantFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;


@Component
public class RequestToken {
    private BadRequestExceptionBuilder badRequestExceptionBuilder;
    private RequestTokenGrantFactory requestTokenGrantFactory;
    private static String GRANT_TYPE = "grant_type";

    @Autowired
    public RequestToken(BadRequestExceptionBuilder badRequestExceptionBuilder, RequestTokenGrantFactory requestTokenGrantFactory) {
        this.badRequestExceptionBuilder = badRequestExceptionBuilder;
        this.requestTokenGrantFactory = requestTokenGrantFactory;
    }

    public TokenResponse request(String clientUserName, String clientPassword, Map<String, String> tokenRequest) throws BadRequestException, UnauthorizedException, NotFoundException, ServerException {
        UUID clientId;
        try {
            clientId = UUID.fromString(clientUserName);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException(ErrorCode.CLIENT_USERNAME_DATA_TYPE.getDescription(), e, ErrorCode.CLIENT_USERNAME_DATA_TYPE.getCode());
        }

        RequestTokenGrant requestTokenGrant = requestTokenGrantFactory.make(tokenRequest.get(GRANT_TYPE));

        if (requestTokenGrant == null) {
            throw badRequestExceptionBuilder.InvalidKeyValue(GRANT_TYPE, ErrorCode.GRANT_TYPE_INVALID.getCode(), null).build();
        }

        TokenResponse tokenResponse = requestTokenGrant.request(clientId, clientPassword, tokenRequest);
        return tokenResponse;
    }
}
