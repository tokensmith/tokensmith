package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.AuthRequest;
import org.rootservices.authorization.persistence.mapper.AuthRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 4/15/15.
 */
@Component
public class AuthRequestRepositoryImpl implements AuthRequestRepository {

    @Autowired
    private AuthRequestMapper authRequestMapper;

    public AuthRequestRepositoryImpl() {}

    public AuthRequestRepositoryImpl(AuthRequestMapper authRequestMapper) {
        this.authRequestMapper = authRequestMapper;
    }

    @Override
    public void insert(AuthRequest authRequest) {
        authRequestMapper.insert(authRequest);
    }
}
