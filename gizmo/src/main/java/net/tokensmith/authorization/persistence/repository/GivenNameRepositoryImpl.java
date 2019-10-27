package net.tokensmith.authorization.persistence.repository;


import net.tokensmith.authorization.persistence.entity.GivenName;
import net.tokensmith.authorization.persistence.mapper.GivenNameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GivenNameRepositoryImpl implements GivenNameRepository {
    private GivenNameMapper givenNameMapper;

    @Autowired
    public GivenNameRepositoryImpl(GivenNameMapper givenNameMapper) {
        this.givenNameMapper = givenNameMapper;
    }

    @Override
    public void insert(GivenName givenName) {
        givenNameMapper.insert(givenName);
    }
}
