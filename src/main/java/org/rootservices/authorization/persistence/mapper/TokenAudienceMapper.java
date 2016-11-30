package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.TokenAudience;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/2/16.
 */
@Repository
public interface TokenAudienceMapper {
    void insert(@Param("clientToken") TokenAudience clientToken);
    TokenAudience getByTokenId(@Param("tokenId") UUID tokenId);
}
