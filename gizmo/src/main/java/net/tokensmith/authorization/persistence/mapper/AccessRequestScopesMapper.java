package net.tokensmith.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import net.tokensmith.authorization.persistence.entity.AccessRequestScope;

/**
 * Created by tommackenzie on 5/19/15.
 */
public interface AccessRequestScopesMapper {
    void insert(@Param("accessRequestScope") AccessRequestScope accessRequestScope);
}
