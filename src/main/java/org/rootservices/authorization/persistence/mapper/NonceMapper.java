package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.Nonce;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NonceMapper {
    void insert(@Param("nonce") Nonce nonce);
    Nonce getById(@Param("id") UUID id);
    Nonce getByTypeAndNonce(@Param("type") String type, @Param("nonce") String nonce);
    Nonce getByNonce(@Param("nonce") String nonce);
    void revokeUnSpent(@Param("type") String type, @Param("resourceOwnerId") UUID resourceOwnerId);
    void setSpent(@Param("id") UUID id);
}
