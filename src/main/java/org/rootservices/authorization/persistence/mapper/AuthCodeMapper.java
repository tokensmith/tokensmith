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
    AuthCode getById(@Param("id") UUID id);
    AuthCode getByClientIdAndAuthCode(@Param("clientId") UUID clientId, @Param("code") String code);
    void revokeById(@Param("id") UUID id);
}
