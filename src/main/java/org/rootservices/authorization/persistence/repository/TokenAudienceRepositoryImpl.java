package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.TokenAudience;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.TokenAudienceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/2/16.
 */
@Component
public class TokenAudienceRepositoryImpl implements TokenAudienceRepository {
    private TokenAudienceMapper clientTokenMapper;

    @Autowired
    public TokenAudienceRepositoryImpl(TokenAudienceMapper clientTokenMapper) {
        this.clientTokenMapper = clientTokenMapper;
    }

    @Override
    public void insert(TokenAudience clientToken) {
        clientTokenMapper.insert(clientToken);
    }

    @Override
    public TokenAudience getByTokenId(UUID tokenId) throws RecordNotFoundException {
        TokenAudience clientToken = clientTokenMapper.getByTokenId(tokenId);
        if (clientToken == null) {
            throw new RecordNotFoundException("Could not find client token");
        }
        return clientToken;
    }
}
