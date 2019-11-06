package net.tokensmith.repository.repo;

import net.tokensmith.repository.entity.TokenAudience;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/2/16.
 */
public interface TokenAudienceRepository {
    void insert(TokenAudience clientToken);
    TokenAudience getByTokenId(UUID tokenId) throws RecordNotFoundException;
}
