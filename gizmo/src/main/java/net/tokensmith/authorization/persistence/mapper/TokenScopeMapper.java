package net.tokensmith.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import net.tokensmith.authorization.persistence.entity.TokenScope;

/**
 * Created by tommackenzie on 4/17/16.
 */
public interface TokenScopeMapper {
    void insert(@Param("tokenScope") TokenScope tokenScope);
}
