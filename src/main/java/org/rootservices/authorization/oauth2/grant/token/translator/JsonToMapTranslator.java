package org.rootservices.authorization.oauth2.grant.token.translator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.token.exception.DuplicateKeyException;
import org.rootservices.authorization.oauth2.grant.token.exception.InvalidPayloadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tommackenzie on 9/21/16.
 */
@Component
public class JsonToMapTranslator {
    private ObjectMapper objectMapper;
    private static final Pattern duplicateFieldPattern = Pattern.compile("Duplicate field \'(\\w+)\'");

    @Autowired
    public JsonToMapTranslator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Map<String, String> to(BufferedReader json) throws DuplicateKeyException, InvalidPayloadException {
        Map<String, String> result = new HashMap<>();
        try {
            result = objectMapper.readValue(json, new TypeReference<Map<String,String>>() { });
        } catch (JsonParseException e) {
            handleJsonParseException(e);
        } catch (IOException e) {
            throw new InvalidPayloadException(
                    ErrorCode.INVALID_PAYLOAD.getDescription(), e, ErrorCode.INVALID_PAYLOAD.getCode()
            );
        }
        return result;
    }

    protected void handleJsonParseException(JsonParseException jpe) throws DuplicateKeyException, InvalidPayloadException {

        Optional<String> duplicateKey = getJsonParseExceptionDuplicateKey(jpe);
        if (duplicateKey.isPresent()) {

            throw new DuplicateKeyException(
                ErrorCode.DUPLICATE_KEY.getDescription(),
                jpe,
                ErrorCode.DUPLICATE_KEY.getCode(),
                duplicateKey.get()
            );
        }

        throw new InvalidPayloadException(
            ErrorCode.INVALID_PAYLOAD.getDescription(),
            jpe,
            ErrorCode.INVALID_PAYLOAD.getCode()
        );
    }

    // TODO: see if jackson can throw a specific exception for duplicates.
    protected Optional<String> getJsonParseExceptionDuplicateKey(JsonParseException e) {
        Optional<String> key = Optional.empty();
        Matcher m = duplicateFieldPattern.matcher(e.getOriginalMessage());
        if (m.matches()) {
            key = Optional.of(m.group(1));
        }
        return key;
    }
}
