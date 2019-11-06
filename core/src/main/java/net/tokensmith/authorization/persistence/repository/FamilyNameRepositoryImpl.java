package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.entity.FamilyName;
import net.tokensmith.authorization.persistence.mapper.FamilyNameMapper;
import net.tokensmith.repository.repo.FamilyNameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FamilyNameRepositoryImpl implements FamilyNameRepository {
    private FamilyNameMapper familyNameMapper;

    @Autowired
    public FamilyNameRepositoryImpl(FamilyNameMapper familyNameMapper) {
        this.familyNameMapper = familyNameMapper;
    }

    @Override
    public void insert(FamilyName familyName) {
        familyNameMapper.insert(familyName);
    }
}
