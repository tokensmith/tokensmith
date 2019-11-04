package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.ResourceOwnerToken;
import org.apache.ibatis.annotations.Param;


public interface ResourceOwnerTokenMapper {
    void insert(@Param("resourceOwnerToken") ResourceOwnerToken resourceOwnerToken);
    ResourceOwnerToken getByAccessToken(@Param("accessToken") String accessToken);
}
