package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.mapper.ConfidentialClientMapper;
import net.tokensmith.repository.entity.ConfidentialClient;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ConfidentialClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 */
@Component
public class ConfidentialClientRepo implements ConfidentialClientRepository {
    private ConfidentialClientMapper confidentialClientMapper;

    @Autowired
    public ConfidentialClientRepo(ConfidentialClientMapper confidentialClientMapper) {
        this.confidentialClientMapper = confidentialClientMapper;
    }

    @Override
    public void insert(ConfidentialClient confidentialClient) {
        confidentialClientMapper.insert(confidentialClient);
    }

    @Override
    public ConfidentialClient getByClientId(UUID clientId) throws RecordNotFoundException {
        ConfidentialClient confidentialClient = confidentialClientMapper.getByClientId(clientId);

        if ( confidentialClient == null)
            throw new RecordNotFoundException("Confidential Client was not found");

        return confidentialClient;
    }
}
