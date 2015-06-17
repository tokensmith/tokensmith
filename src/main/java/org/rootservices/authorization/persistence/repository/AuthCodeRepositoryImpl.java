package org.rootservices.authorization.persistence.repository;

import com.sun.prism.impl.Disposer;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.AuthCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.UUID;

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

    @Override
    public AuthCode getByClientUUIDAndAuthCode(UUID clientUUID, String code) throws RecordNotFoundException {
        AuthCode authCode = authCodeMapper.getByClientUUIDAndAuthCode(clientUUID, code);

        if (authCode == null) {
            throw new RecordNotFoundException("AuthCode record was not found.");
        }

        return authCode;
    }


}
