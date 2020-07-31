package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.mapper.AccessRequestMapper;
import net.tokensmith.authorization.persistence.mapper.HealthMapper;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.jwe.serialization.JweDeserializer;
import net.tokensmith.repository.entity.AccessRequest;
import net.tokensmith.repository.repo.AccessRequestRepository;
import net.tokensmith.repository.repo.CipherRepository;
import net.tokensmith.repository.repo.HealthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class HealthRepo implements HealthRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(HealthRepo.class);
    private static Integer OK = 1;
    private HealthMapper healthMapper;

    @Autowired
    public HealthRepo(HealthMapper healthMapper) {
        this.healthMapper = healthMapper;
    }


    @Override
    public Boolean isOk() {
        Integer ok = healthMapper.ok();
        if (OK.equals(ok)) {
            return true;
        }
        return false;
    }
}
