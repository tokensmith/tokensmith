package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.Name;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

/**
 * Created by tommackenzie on 2/25/16.
 */
public interface GivenNameMapper {
    void insert(@Param("givenName") Name givenName);
    Name findById(@Param("id") UUID id);
    void update(@Param("resourceOwnerId") UUID resourceOwnerId, @Param("givenName") Name givenName);
    void delete(@Param("resourceOwnerId") UUID resourceOwnerId, @Param("givenName") Name givenName);
}
