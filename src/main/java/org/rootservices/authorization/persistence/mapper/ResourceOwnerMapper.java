package org.rootservices.authorization.persistence.mapper;


import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Created by tommackenzie on 9/24/14.
 */
@Repository
public interface ResourceOwnerMapper {
    ResourceOwner getById(@Param("id") UUID id);
    ResourceOwner getByEmail(@Param("email") String email);
    ResourceOwner getByAccessToken(@Param("accessToken") String accessToken);
    ResourceOwner getByAccessTokenWithProfileAndTokens(@Param("accessToken") String accessToken);
    void insert(@Param("resourceOwner") ResourceOwner resourceOwner);
}
