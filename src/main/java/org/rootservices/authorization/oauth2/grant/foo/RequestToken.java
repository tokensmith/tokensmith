package org.rootservices.authorization.oauth2.grant.foo;

import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.foo.entity.TokenResponse;
import org.rootservices.authorization.oauth2.grant.foo.exception.*;
import org.rootservices.authorization.oauth2.grant.foo.factory.RequestTokenFactory;
import org.rootservices.authorization.oauth2.grant.foo.translator.JsonToMapTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.util.Map;
import java.util.UUID;

/**
 * Created by tommackenzie on 9/27/16.
 */
@Component
public class RequestToken {
    private JsonToMapTranslator jsonToMapTranslator;
    private BadRequestExceptionBuilder badRequestExceptionBuilder;
    private RequestTokenFactory requestTokenFactory;
    private static String GRANT_TYPE = "grant_type";

    @Autowired
    public RequestToken(JsonToMapTranslator jsonToMapTranslator, BadRequestExceptionBuilder badRequestExceptionBuilder, RequestTokenFactory requestTokenFactory) {
        this.jsonToMapTranslator = jsonToMapTranslator;
        this.badRequestExceptionBuilder = badRequestExceptionBuilder;
        this.requestTokenFactory = requestTokenFactory;
    }

    public TokenResponse request(UUID clientId, String clientPassword, BufferedReader request) throws BadRequestException, UnauthorizedException {

        Map<String, String> tokenInput = null;
        try {
            tokenInput = jsonToMapTranslator.to(request);
        } catch (DuplicateKeyException e) {
            throw badRequestExceptionBuilder.DuplicateKey(e.getKey(), e.getCode(), e).build();
        } catch (InvalidPayloadException e) {
            throw badRequestExceptionBuilder.InvalidPayload(e.getCode(), e).build();
        }

        RequestTokenGrant requestTokenGrant = requestTokenFactory.make(tokenInput.get(GRANT_TYPE));

        if (requestTokenGrant == null) {
            throw badRequestExceptionBuilder.InvalidKeyValue(GRANT_TYPE, ErrorCode.GRANT_TYPE_INVALID.getCode(), null).build();
        }

        TokenResponse tokenResponse = requestTokenGrant.request(clientId, clientPassword, tokenInput);
        return tokenResponse;
    }
}
