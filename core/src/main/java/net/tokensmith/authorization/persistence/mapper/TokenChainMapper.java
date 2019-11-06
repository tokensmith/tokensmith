package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.TokenChain;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/8/16.
 */
public interface TokenChainMapper {
    TokenChain getById(@Param("id") UUID id);
    void insert(@Param("tokenChain") TokenChain tokenChain);
}
