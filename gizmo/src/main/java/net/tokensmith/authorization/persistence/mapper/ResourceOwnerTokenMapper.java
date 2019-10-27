package net.tokensmith.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import net.tokensmith.authorization.persistence.entity.ResourceOwnerToken;


public interface ResourceOwnerTokenMapper {
    void insert(@Param("resourceOwnerToken") ResourceOwnerToken resourceOwnerToken);
    ResourceOwnerToken getByAccessToken(@Param("accessToken") String accessToken);
}
