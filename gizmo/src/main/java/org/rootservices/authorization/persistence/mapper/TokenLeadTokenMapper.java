package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.TokenLeadToken;

import java.util.UUID;

/**
 * Created by tommackenzie on 12/1/16.
 */
public interface TokenLeadTokenMapper {
    TokenLeadToken getById(@Param("id") UUID id);
    void insert(@Param("tokenLeadToken") TokenLeadToken tokenLeadToken);
}
