package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.TokenScope;
import org.apache.ibatis.annotations.Param;

/**
 * Created by tommackenzie on 4/17/16.
 */
public interface TokenScopeMapper {
    void insert(@Param("tokenScope") TokenScope tokenScope);
}
