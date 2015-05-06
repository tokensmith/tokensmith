package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.mapper.AuthCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * Created by tommackenzie on 4/10/15.
 */
@Component
public class AuthCodeRepositoryImpl implements AuthCodeRepository {

    @Autowired
    private AuthCodeMapper authCodeMapper;

    public AuthCodeRepositoryImpl() {}

    public AuthCodeRepositoryImpl(AuthCodeMapper authCodeMapper) {
        this.authCodeMapper = authCodeMapper;
    }

    @Override
    public void insert(AuthCode authCode) {
        authCodeMapper.insert(authCode);
    }
}
