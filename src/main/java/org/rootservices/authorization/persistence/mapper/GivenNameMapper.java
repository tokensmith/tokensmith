package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.GivenName;

import java.util.UUID;

/**
 * Created by tommackenzie on 2/25/16.
 */
public interface GivenNameMapper {
    void insert(@Param("givenName") GivenName givenName);
    GivenName findById(@Param("id") UUID id);
}
