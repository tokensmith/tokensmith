package org.rootservices.authorization.oauth2.grant.redirect.code.token.factory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.TokenRequest;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.factory.exception.DuplicateKeyException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.factory.exception.UnknownKeyException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.validator.IsTokenRequestValid;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.validator.exception.InvalidValueException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.validator.exception.MissingKeyException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.factory.exception.InvalidPayloadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tommackenzie on 6/29/15.
 *
 */
@Component
public class JsonToTokenRequest {

    private ObjectMapper objectMapper;
    private IsTokenRequestValid isTokenRequestValid;

    @Autowired
    public JsonToTokenRequest(ObjectMapper objectMapper, IsTokenRequestValid isTokenRequestValid) {
        this.objectMapper = objectMapper;
        this.isTokenRequestValid = isTokenRequestValid;
    }

    public TokenRequest run(BufferedReader json) throws DuplicateKeyException, InvalidPayloadException, InvalidValueException, MissingKeyException, UnknownKeyException {
        TokenRequest tokenRequest = null;
        try {
            // Map<String, String> result = objectMapper.readValue(json, new TypeReference<Map<String,String>>() { });
            tokenRequest = objectMapper.readValue(json, TokenRequest.class);
        } catch (JsonParseException e) {
            // TODO: see if jackson can throw a specific exception for duplicates.
            Optional<String> duplicateKey = getJsonParseExceptionDuplicateKey(e);
            if (duplicateKey.isPresent()) {
                throw new DuplicateKeyException(
                    ErrorCode.DUPLICATE_KEY.getDescription(), e, ErrorCode.DUPLICATE_KEY.getCode(), duplicateKey.get()
                );
            }

            throw new InvalidPayloadException(
                    ErrorCode.INVALID_PAYLOAD.getDescription(), e, ErrorCode.INVALID_PAYLOAD.getCode()
            );
        } catch (UnrecognizedPropertyException e) {
            throw new UnknownKeyException(
                    ErrorCode.UNKNOWN_KEY.getDescription(), e.getPropertyName(), e, ErrorCode.UNKNOWN_KEY.getCode()
            );
        } catch (IOException e) {
            throw new InvalidPayloadException(
                    ErrorCode.INVALID_PAYLOAD.getDescription(), e, ErrorCode.INVALID_PAYLOAD.getCode()
            );
        }

        isTokenRequestValid.run(tokenRequest);

        return tokenRequest;
    }

    private Optional<String> getJsonParseExceptionDuplicateKey(JsonParseException e) {
        Optional<String> key = Optional.empty();
        Pattern p = Pattern.compile("Duplicate field \'(\\w+)\'");
        Matcher m = p.matcher(e.getMessage());
        if (m.matches()) {
            key = Optional.of(m.group(1));
        }
        return key;
    }

}
