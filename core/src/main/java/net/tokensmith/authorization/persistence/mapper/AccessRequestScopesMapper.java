package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.AccessRequestScope;
import org.apache.ibatis.annotations.Param;

/**
 * Created by tommackenzie on 5/19/15.
 */
public interface AccessRequestScopesMapper {
    void insert(@Param("accessRequestScope") AccessRequestScope accessRequestScope);
}
