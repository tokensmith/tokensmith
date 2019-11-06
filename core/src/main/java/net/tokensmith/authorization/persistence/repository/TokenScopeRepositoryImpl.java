package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.entity.TokenScope;
import net.tokensmith.authorization.persistence.mapper.TokenScopeMapper;
import net.tokensmith.repository.repo.TokenScopeRepository;
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
