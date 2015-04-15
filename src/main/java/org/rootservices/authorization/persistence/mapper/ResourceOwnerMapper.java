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
    public ResourceOwner getByUUID(@Param("uuid") UUID uuid);
    public ResourceOwner getByEmailAndPassword(@Param("email") String email, @Param("password") byte[] password);
    public void insert(@Param("authUser") ResourceOwner authUser);
}
