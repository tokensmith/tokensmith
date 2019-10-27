package net.tokensmith.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import net.tokensmith.authorization.persistence.entity.NonceType;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NonceTypeMapper {
    void insert(@Param("nonceType") NonceType nonceType);
    NonceType getById(@Param("id") UUID id);
    NonceType getByName(@Param("name") String name);
}
