package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/8/15.
 */
@Repository
public interface AuthCodeMapper {
    void insert(@Param("authCode") AuthCode authCode);
    AuthCode getByClientUUIDAndAuthCode(@Param("clientUUID") UUID clientUUID, @Param("code") String code);
}
