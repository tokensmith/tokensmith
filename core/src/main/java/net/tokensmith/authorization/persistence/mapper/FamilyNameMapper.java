package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.Name;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

/**
 * Created by tommackenzie on 3/19/16.
 */
public interface FamilyNameMapper {
    void insert(@Param("familyName") Name familyName);
    Name findById(@Param("id") UUID id);
    void update(@Param("resourceOwnerId") UUID resourceOwnerId, @Param("familyName") Name familyName);
    void delete(@Param("resourceOwnerId") UUID resourceOwnerId, @Param("familyName") Name familyName);
}
