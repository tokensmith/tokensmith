package net.tokensmith.authorization.persistence.mapper;


import net.tokensmith.authorization.persistence.entity.ResourceOwner;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface ResourceOwnerMapper {
    ResourceOwner getById(@Param("id") UUID id);
    ResourceOwner getByEmail(@Param("email") String email);
    ResourceOwner getByAccessToken(@Param("accessToken") String accessToken);
    ResourceOwner getByAccessTokenWithProfileAndTokens(@Param("accessToken") String accessToken);
    void insert(@Param("resourceOwner") ResourceOwner resourceOwner);
    void setEmailVerified(@Param("id") UUID id);
    void updatePassword(@Param("id") UUID id, @Param("password") byte[] password);
}
