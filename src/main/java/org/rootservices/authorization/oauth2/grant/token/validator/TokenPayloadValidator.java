package org.rootservices.authorization.oauth2.grant.token.validator;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.token.exception.InvalidValueException;
import org.rootservices.authorization.oauth2.grant.token.exception.MissingKeyException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by tommackenzie on 9/21/16.
 */
@Component
public class TokenPayloadValidator {
    private static String MISSING_KEY_MSG = "missing key ";

    public String required(String input, String key) throws MissingKeyException{
        if (input == null || input.isEmpty()){
            throw new MissingKeyException(MISSING_KEY_MSG + key, key);
        }
        return input;
    }

    public Optional<String> optional(String input, String key) throws InvalidValueException{
        Optional<String> output = Optional.empty();
        if (input != null && input.isEmpty()){
            throw new InvalidValueException(
                    ErrorCode.EMPTY_VALUE.getDescription(),
                    ErrorCode.EMPTY_VALUE.getCode(),
                    key,
                    input
            );
        }

        if (input != null) {
            output = Optional.of(input);
        }
        return output;
    }

    public Optional<String> getFirstUnknownKey(Map<String, String> input, List<String> knownKeys) {
        Set<String> keys = input.keySet();

        Optional<String> unknownKey = keys.stream().filter(key -> !knownKeys.contains(key))
                .findFirst();

        return unknownKey;
    }
}
