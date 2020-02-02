package net.tokensmith.authorization.persistence.repository;


import net.tokensmith.authorization.persistence.mapper.GivenNameMapper;
import net.tokensmith.repository.entity.Name;
import net.tokensmith.repository.repo.GivenNameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.UUID;

@Component
public class GivenNameRepo implements GivenNameRepository {
    private GivenNameMapper givenNameMapper;

    @Autowired
    public GivenNameRepo(GivenNameMapper givenNameMapper) {
        this.givenNameMapper = givenNameMapper;
    }

    @Override
    public void insert(Name givenName) {
        givenNameMapper.insert(givenName);
    }

    @Override
    public void update(UUID resourceOwnerId, Name givenName) {
        givenNameMapper.update(resourceOwnerId, givenName);
    }

    @Override
    public void delete(UUID resourceOwnerId, Name givenName) {
        givenNameMapper.delete(resourceOwnerId, givenName);
    }
}
