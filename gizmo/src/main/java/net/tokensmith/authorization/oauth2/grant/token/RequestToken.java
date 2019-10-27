package net.tokensmith.authorization.oauth2.grant.token;

import net.tokensmith.authorization.authenticate.exception.UnauthorizedException;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.exception.BadRequestException;
import net.tokensmith.authorization.exception.ServerException;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenResponse;
import net.tokensmith.authorization.oauth2.grant.token.exception.*;
import net.tokensmith.authorization.oauth2.grant.token.factory.RequestTokenGrantFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;


@Component
public class RequestToken {
    private RequestTokenGrantFactory requestTokenGrantFactory;
    private static String GRANT_TYPE = "grant_type";

    @Autowired
    public RequestToken(RequestTokenGrantFactory requestTokenGrantFactory) {
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
            throw new BadRequestExceptionBuilder().InvalidKeyValue(GRANT_TYPE, ErrorCode.GRANT_TYPE_INVALID.getCode(), null).build();
        }

        TokenResponse tokenResponse = requestTokenGrant.request(clientId, clientPassword, tokenRequest);
        return tokenResponse;
    }
}
