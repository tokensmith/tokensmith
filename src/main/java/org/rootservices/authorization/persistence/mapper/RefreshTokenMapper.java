package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.Profile;
import org.rootservices.authorization.persistence.entity.RefreshToken;
import org.springframework.stereotype.Repository;

/**
 * Created by tommackenzie on 10/2/16.
 */
@Repository
public interface RefreshTokenMapper {
    void insert(@Param("refreshToken") RefreshToken refreshToken);
    RefreshToken getByToken(@Param("token") String token);
}
