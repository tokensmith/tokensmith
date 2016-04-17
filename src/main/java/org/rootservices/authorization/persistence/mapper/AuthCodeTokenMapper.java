package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.AuthCodeToken;

/**
 * Created by tommackenzie on 4/16/16.
 */
public interface AuthCodeTokenMapper {
    void insert(@Param("authCodeToken") AuthCodeToken authCodeToken);
}
