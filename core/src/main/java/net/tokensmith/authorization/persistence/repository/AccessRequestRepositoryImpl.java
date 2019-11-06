package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.entity.AccessRequest;
import net.tokensmith.authorization.persistence.mapper.AccessRequestMapper;
import net.tokensmith.repository.repo.AccessRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
}
