package net.tokensmith.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import net.tokensmith.authorization.persistence.entity.AccessRequest;
import org.springframework.stereotype.Repository;

/**
 * Created by tommackenzie on 4/15/15.
 */
@Repository
public interface AccessRequestMapper {
    void insert(@Param("accessRequest") AccessRequest accessRequest);
}
