package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.TokenChain;
import org.springframework.dao.DuplicateKeyException;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/8/16.
 */
public interface TokenChainMapper {
    TokenChain getById(@Param("id") UUID id);
    void insert(@Param("tokenChain") TokenChain tokenChain);
}
