package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.TokenLeadToken;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

/**
 * Created by tommackenzie on 12/1/16.
 */
public interface TokenLeadTokenMapper {
    TokenLeadToken getById(@Param("id") UUID id);
    void insert(@Param("tokenLeadToken") TokenLeadToken tokenLeadToken);
}
