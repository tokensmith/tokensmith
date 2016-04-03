package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.AccessRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/15/15.
 */
@Component
public class AccessRequestRepositoryImpl implements AccessRequestRepository {

    @Autowired
    private AccessRequestMapper accessRequestMapper;

    public AccessRequestRepositoryImpl() {}

    public AccessRequestRepositoryImpl(AccessRequestMapper accessRequestMapper) {
        this.accessRequestMapper = accessRequestMapper;
    }

    @Override
    public void insert(AccessRequest accessRequest) {
        accessRequestMapper.insert(accessRequest);
    }

    @Override
    public AccessRequest getByAccessToken(String accessToken) throws RecordNotFoundException {
        AccessRequest accessRequest = accessRequestMapper.getByAccessToken(accessToken.getBytes());

        if (accessRequest == null) {
            throw new RecordNotFoundException("Could not find access request");
        }
        return accessRequest;

    }


}
