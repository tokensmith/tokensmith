package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.TokenScope;
import org.rootservices.authorization.persistence.mapper.TokenScopeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 4/18/16.
 */
@Component
public class TokenScopeRepositoryImpl implements TokenScopeRepository {
    private TokenScopeMapper tokenScopeMapper;

    @Autowired
    public TokenScopeRepositoryImpl(TokenScopeMapper tokenScopeMapper) {
        this.tokenScopeMapper = tokenScopeMapper;
    }

    @Override
    public void insert(TokenScope tokenScope) {
        tokenScopeMapper.insert(tokenScope);
    }
}
