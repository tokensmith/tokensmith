package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.AuthRequest;
import org.springframework.stereotype.Repository;

/**
 * Created by tommackenzie on 4/15/15.
 */
@Repository
public interface AuthRequestMapper {
    public void insert(@Param("authRequest") AuthRequest authRequest);
}
