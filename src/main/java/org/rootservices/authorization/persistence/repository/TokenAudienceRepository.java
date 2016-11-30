package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.TokenAudience;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/2/16.
 */
public interface TokenAudienceRepository {
    void insert(TokenAudience clientToken);
    TokenAudience getByTokenId(UUID tokenId) throws RecordNotFoundException;
}
