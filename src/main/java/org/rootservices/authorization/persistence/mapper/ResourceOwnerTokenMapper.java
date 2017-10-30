package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.ResourceOwnerToken;

import java.util.UUID;


public interface ResourceOwnerTokenMapper {
    void insert(@Param("resourceOwnerToken") ResourceOwnerToken resourceOwnerToken);
    ResourceOwnerToken getByAccessToken(@Param("accessToken") String accessToken);
}
