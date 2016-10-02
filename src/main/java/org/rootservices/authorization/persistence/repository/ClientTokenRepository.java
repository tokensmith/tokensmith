package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.ClientToken;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/2/16.
 */
public interface ClientTokenRepository {
    void insert(ClientToken clientToken);
    ClientToken getByTokenId(UUID tokenId) throws RecordNotFoundException;
}
