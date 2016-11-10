package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.TokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tommackenzie on 5/23/15.
 */
@Component
public class TokenRepositoryImpl implements TokenRepository {
    private static String DUPLICATE_RECORD_MSG = "Could not insert token record.";
    private static String RECORD_NOT_FOUND_MSG = "Could not find token record.";

    private Pattern uniqueKeyPattern = Pattern.compile(".*Detail: Key \\((\\w+)\\).*", Pattern.DOTALL);

    private TokenMapper tokenMapper;

    @Autowired
    public TokenRepositoryImpl(TokenMapper tokenMapper) {
        this.tokenMapper = tokenMapper;
    }

    @Override
    public void insert(Token token) throws DuplicateRecordException {
        try {
            tokenMapper.insert(token);
        } catch (DuplicateKeyException e) {
            Matcher matcher = uniqueKeyPattern.matcher(e.getMessage());
            Optional<String> key = Optional.empty();
            if (matcher.matches()) {
                key = Optional.of(matcher.group(1));
            }
            throw new DuplicateRecordException(DUPLICATE_RECORD_MSG, e, key);
        }
    }

    @Override
    public void revokeByAuthCodeId(UUID authCodeId) {
        tokenMapper.revokeByAuthCodeId(authCodeId);
    }

    @Override
    public void revokeById(UUID id) {
        tokenMapper.revokeById(id);
    }

    @Override
    public void updateExpiresAtByAccessToken(OffsetDateTime expiresAt, String accessToken) {
        tokenMapper.updateExpiresAtByAccessToken(expiresAt, accessToken);
    }

    @Override
    public Token getByAuthCodeId(UUID authCodeId) throws RecordNotFoundException {
        Token token = tokenMapper.getByAuthCodeId(authCodeId);

        if (token == null) {
            throw new RecordNotFoundException(RECORD_NOT_FOUND_MSG);
        }

        return token;
    }
}
