package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.entity.Name;
import net.tokensmith.authorization.persistence.mapper.FamilyNameMapper;
import net.tokensmith.repository.repo.FamilyNameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FamilyNameRepo implements FamilyNameRepository {
    private FamilyNameMapper familyNameMapper;

    @Autowired
    public FamilyNameRepo(FamilyNameMapper familyNameMapper) {
        this.familyNameMapper = familyNameMapper;
    }

    @Override
    public void insert(Name name) {
        familyNameMapper.insert(name);
    }

    @Override
    public void update(UUID resourceOwnerId, Name name) {
        familyNameMapper.update(resourceOwnerId, name);
    }

    @Override
    public void delete(UUID resourceOwnerId, Name name) {
        familyNameMapper.delete(resourceOwnerId, name);
    }
}
