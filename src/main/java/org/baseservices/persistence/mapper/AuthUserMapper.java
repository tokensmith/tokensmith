package org.baseservices.persistence.mapper;


import org.baseservices.persistence.entity.AuthUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Created by tommackenzie on 9/24/14.
 */
@Repository
public interface AuthUserMapper {

    public AuthUser getByUUID(@Param("uuid") UUID uuid);
    public AuthUser getByEmailAndPassword(@Param("email") String email, @Param("password") byte[] password);
    public void insert(@Param("authUser") AuthUser authUser);
    public void update(AuthUser authUser);

}
