package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.FamilyName;

import java.util.UUID;

/**
 * Created by tommackenzie on 3/19/16.
 */
public interface FamilyNameMapper {
    void insert(@Param("familyName") FamilyName familyName);
    FamilyName findById(@Param("id") UUID id);
}
