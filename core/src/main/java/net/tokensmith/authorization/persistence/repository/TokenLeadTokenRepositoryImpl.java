package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.entity.TokenLeadToken;
import net.tokensmith.authorization.persistence.mapper.TokenLeadTokenMapper;
import net.tokensmith.repository.repo.TokenLeadTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 12/1/16.
 */
@Component
public class TokenLeadTokenRepositoryImpl implements TokenLeadTokenRepository {
    private TokenLeadTokenMapper tokenLeadTokenMapper;

    @Autowired
    public TokenLeadTokenRepositoryImpl(TokenLeadTokenMapper tokenLeadTokenMapper) {
        this.tokenLeadTokenMapper = tokenLeadTokenMapper;
    }

    @Override
    public void insert(TokenLeadToken tokenLeadToken) {
        tokenLeadTokenMapper.insert(tokenLeadToken);
    }
}
