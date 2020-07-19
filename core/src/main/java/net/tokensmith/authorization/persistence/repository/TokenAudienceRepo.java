package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.mapper.TokenAudienceMapper;
import net.tokensmith.repository.entity.TokenAudience;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.TokenAudienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/2/16.
 */
@Component
public class TokenAudienceRepo implements TokenAudienceRepository {
    private TokenAudienceMapper clientTokenMapper;

    @Autowired
    public TokenAudienceRepo(TokenAudienceMapper clientTokenMapper) {
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
