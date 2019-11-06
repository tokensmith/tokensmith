package net.tokensmith.authorization.persistence.factory;

import net.tokensmith.repository.exceptions.DuplicateRecordException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tommackenzie on 11/11/16.
 */
@Component
public class DuplicateRecordExceptionFactory {
    private static Pattern psqlPattern = Pattern.compile(".*Detail: Key \\((\\w+)\\).*", Pattern.DOTALL);
    private static String DUPLICATE_RECORD_MSG = "Could not insert %s record. %s";
    private static String KEY_FOUND_MSG = "The key, %s, would have caused a duplicate record.";
    private static String KEY_NOT_FOUND_MSG = "Unable to determine the key that would have caused the duplicate record.";

    public DuplicateRecordException make(DuplicateKeyException dke, String schema) {
        String messageForKey;
        Matcher matcher = psqlPattern.matcher(dke.getMessage());
        Optional<String> key = Optional.empty();
        if (matcher.matches()) {
            key = Optional.of(matcher.group(1));
            messageForKey = String.format(KEY_FOUND_MSG, key.get());
        } else {
            messageForKey = KEY_NOT_FOUND_MSG;
        }

        return new DuplicateRecordException(
                String.format(DUPLICATE_RECORD_MSG, schema, messageForKey),
                dke,
                key
        );
    }
}
