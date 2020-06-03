package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.LocalToken;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface LocalTokenMapper {
    LocalToken getById(@Param("id") UUID id);
    void insert(@Param("localToken") LocalToken localToken);
    void revokeActive(@Param("resourceOwnerId") UUID resourceOwnerId);
}
