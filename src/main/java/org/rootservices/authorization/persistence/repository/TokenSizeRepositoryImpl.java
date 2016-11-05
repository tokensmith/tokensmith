package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.TokenSize;
import org.rootservices.authorization.persistence.mapper.TokenSizeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 11/5/16.
 */
@Component
public class TokenSizeRepositoryImpl implements TokenSizeRepository {
    private TokenSizeMapper tokenSizeMapper;

    @Autowired
    public TokenSizeRepositoryImpl(TokenSizeMapper tokenSizeMapper) {
        this.tokenSizeMapper = tokenSizeMapper;
    }

    @Override
    public TokenSize get() {
        return tokenSizeMapper.get();
    }
}
