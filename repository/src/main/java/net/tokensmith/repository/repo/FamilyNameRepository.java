package net.tokensmith.repository.repo;


import net.tokensmith.repository.entity.Name;

import java.util.UUID;

public interface FamilyNameRepository {
    void insert(Name name);
    void update(UUID resourceOwnerId, Name name);
    void delete(UUID resourceOwnerId, Name name);
}
