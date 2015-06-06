package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.ConfidentialClientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 */
@Component
public class ConfidentialClientRepositoryImpl implements ConfidentialClientRepository {
    private ConfidentialClientMapper confidentialClientMapper;

    @Autowired
    public ConfidentialClientRepositoryImpl(ConfidentialClientMapper confidentialClientMapper) {
        this.confidentialClientMapper = confidentialClientMapper;
    }

    @Override
    public void insert(ConfidentialClient confidentialClient) {
        confidentialClientMapper.insert(confidentialClient);
    }

    @Override
    public ConfidentialClient getByClientUUID(UUID clientUUID) throws RecordNotFoundException {
        ConfidentialClient confidentialClient = confidentialClientMapper.getByClientUUID(clientUUID);

        if ( confidentialClient == null)
            throw new RecordNotFoundException("Confidential Client was not found");

        return confidentialClient;
    }
}
