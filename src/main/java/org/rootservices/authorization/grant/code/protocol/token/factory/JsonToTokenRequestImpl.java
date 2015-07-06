package org.rootservices.authorization.grant.code.protocol.token.factory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.token.TokenRequest;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.DuplicateKeyException;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.InvalidPayloadException;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.InvalidValueException;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.MissingKeyException;
import org.rootservices.authorization.grant.code.protocol.token.validator.IsTokenRequestValid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tommackenzie on 6/29/15.
 *
 */
@Component
public class JsonToTokenRequestImpl implements JsonToTokenRequest {

    private ObjectMapper objectMapper;
    private IsTokenRequestValid isTokenRequestValid;

    @Autowired
    public JsonToTokenRequestImpl(ObjectMapper objectMapper, IsTokenRequestValid isTokenRequestValid){
        this.objectMapper = objectMapper;
        this.isTokenRequestValid = isTokenRequestValid;
    }

    @Override
    public TokenRequest run(BufferedReader json) throws DuplicateKeyException, InvalidPayloadException, InvalidValueException, MissingKeyException {
        TokenRequest tokenRequest = null;
        try {
            tokenRequest = objectMapper.readValue(json, TokenRequest.class);
        } catch (JsonParseException e) {
            // TODO: see if jackson will throw a more specific exception.
            Pattern p = Pattern.compile("Duplicate field \'(\\w+)\'\\n(.*)");
            Matcher m = p.matcher(e.getMessage());
            if ( m.matches() ) {
                String key = m.group(1);
                DuplicateKeyException dke = new DuplicateKeyException(
                    ErrorCode.DUPLICATE_KEY.getMessage(), e, ErrorCode.DUPLICATE_KEY.getCode(), key
                );
                throw dke;
            }
            // TODO: Throw InvalidPayload here.
        } catch (IOException e) {
            throw new InvalidPayloadException(
                ErrorCode.INVALID_PAYLOAD.getMessage(), e, ErrorCode.INVALID_PAYLOAD.getCode()
            );
        }

        isTokenRequestValid.run(tokenRequest);

        return tokenRequest;
    }

}
