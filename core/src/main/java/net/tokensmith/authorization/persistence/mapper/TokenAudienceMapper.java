package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.TokenAudience;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/2/16.
 */
@Repository
public interface TokenAudienceMapper {
    void insert(@Param("tokenAudience") TokenAudience tokenAudience);
    TokenAudience getByTokenId(@Param("tokenId") UUID tokenId);
}
