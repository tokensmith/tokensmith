package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.mapper.AccessRequestMapper;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.jwe.serialization.JweDeserializer;
import net.tokensmith.repository.entity.AccessRequest;
import net.tokensmith.repository.repo.AccessRequestRepository;
import net.tokensmith.repository.repo.CipherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 4/15/15.
 */
@Component
public class AccessRequestRepo implements AccessRequestRepository, CipherRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessRequestRepo.class);

    private AccessRequestMapper accessRequestMapper;
    private JwtAppFactory jwtAppFactory;
    private SymmetricKey dbKey;

    @Autowired
    public AccessRequestRepo(AccessRequestMapper accessRequestMapper, JwtAppFactory jwtAppFactory, SymmetricKey dbKey) {
        this.accessRequestMapper = accessRequestMapper;
        this.jwtAppFactory = jwtAppFactory;
        this.dbKey = dbKey;
    }

    @Override
    public void insert(AccessRequest accessRequest) {
        // 150: does the nonce need to be encrypted?
        accessRequestMapper.insert(accessRequest);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public JweDeserializer getDeserializer() {
        return jwtAppFactory.jweDirectDesializer();
    }

    @Override
    public SymmetricKey getKey() {
        return this.dbKey;
    }
}
