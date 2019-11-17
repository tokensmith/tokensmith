package net.tokensmith.authorization.persistence.repository;


import net.tokensmith.repository.entity.GivenName;
import net.tokensmith.authorization.persistence.mapper.GivenNameMapper;
import net.tokensmith.repository.repo.GivenNameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GivenNameRepo implements GivenNameRepository {
    private GivenNameMapper givenNameMapper;

    @Autowired
    public GivenNameRepo(GivenNameMapper givenNameMapper) {
        this.givenNameMapper = givenNameMapper;
    }

    @Override
    public void insert(GivenName givenName) {
        givenNameMapper.insert(givenName);
    }
}
