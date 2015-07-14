package org.rootservices.authorization.grant.code.protocol.token.factory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.token.TokenRequest;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.*;
import org.rootservices.authorization.grant.code.protocol.token.validator.IsTokenRequestValid;
import org.rootservices.authorization.grant.code.protocol.token.validator.exception.InvalidValueException;
import org.rootservices.authorization.grant.code.protocol.token.validator.exception.MissingKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
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
    public JsonToTokenRequestImpl(ObjectMapper objectMapper, IsTokenRequestValid isTokenRequestValid) {
        this.objectMapper = objectMapper;
        this.isTokenRequestValid = isTokenRequestValid;
    }

    @Override
    public TokenRequest run(BufferedReader json) throws DuplicateKeyException, InvalidPayloadException, InvalidValueException, MissingKeyException, UnknownKeyException {
        TokenRequest tokenRequest = null;
        try {
            tokenRequest = objectMapper.readValue(json, TokenRequest.class);
        } catch (JsonParseException e) {
            // TODO: see if jackson can throw a specific exception for duplicates.
            Optional<String> duplicateKey = getJsonParseExceptionDuplicateKey(e);
            if (duplicateKey.isPresent()) {
                throw new DuplicateKeyException(
                    ErrorCode.DUPLICATE_KEY.getMessage(), e, ErrorCode.DUPLICATE_KEY.getCode(), duplicateKey.get()
                );
            }

            throw new InvalidPayloadException(
                    ErrorCode.INVALID_PAYLOAD.getMessage(), e, ErrorCode.INVALID_PAYLOAD.getCode()
            );
        } catch (UnrecognizedPropertyException e) {
            throw new UnknownKeyException(
                    ErrorCode.UNKNOWN_KEY.getMessage(), e.getPropertyName(), e, ErrorCode.UNKNOWN_KEY.getCode()
            );
        } catch (IOException e) {
            throw new InvalidPayloadException(
                    ErrorCode.INVALID_PAYLOAD.getMessage(), e, ErrorCode.INVALID_PAYLOAD.getCode()
            );
        }

        isTokenRequestValid.run(tokenRequest);

        return tokenRequest;
    }

    private Optional<String> getJsonParseExceptionDuplicateKey(JsonParseException e) {
        Optional<String> key = Optional.empty();
        Pattern p = Pattern.compile("Duplicate field \'(\\w+)\'\\n(.*)");
        Matcher m = p.matcher(e.getMessage());
        if (m.matches()) {
            key = Optional.of(m.group(1));
        }
        return key;
    }

}
