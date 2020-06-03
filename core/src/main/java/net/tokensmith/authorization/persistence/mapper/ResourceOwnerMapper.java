package net.tokensmith.authorization.persistence.mapper;


import net.tokensmith.repository.entity.ResourceOwner;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface ResourceOwnerMapper {
    ResourceOwner getById(@Param("id") UUID id);
    ResourceOwner getByLocalToken(@Param("token") String token);
    ResourceOwner getByEmail(@Param("email") String email);
    ResourceOwner getByAccessToken(@Param("accessToken") String accessToken);
    ResourceOwner getByAccessTokenWithProfileAndTokens(@Param("accessToken") String accessToken);
    ResourceOwner getByIdWithProfile(@Param("id") UUID id);
    void insert(@Param("resourceOwner") ResourceOwner resourceOwner);
    void setEmailVerified(@Param("id") UUID id);
    void updatePassword(@Param("id") UUID id, @Param("password") String password);
    void updateEmail(@Param("id") UUID id, @Param("email") String email);
}
