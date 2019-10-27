package net.tokensmith.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import net.tokensmith.authorization.persistence.entity.AuthCodeToken;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/16/16.
 */
public interface AuthCodeTokenMapper {
    void insert(@Param("authCodeToken") AuthCodeToken authCodeToken);
    AuthCodeToken getByTokenId(@Param("tokenId") UUID tokenId);
}
