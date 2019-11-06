package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.FamilyName;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

/**
 * Created by tommackenzie on 3/19/16.
 */
public interface FamilyNameMapper {
    void insert(@Param("familyName") FamilyName familyName);
    FamilyName findById(@Param("id") UUID id);
}
