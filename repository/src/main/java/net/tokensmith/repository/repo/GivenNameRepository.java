package net.tokensmith.repository.repo;


import net.tokensmith.repository.entity.Name;

import java.util.UUID;

public interface GivenNameRepository {
    void insert(Name givenName);
    void update(UUID resourceOwnerId, Name givenName);
    void delete(UUID resourceOwnerId, Name givenName);
}
