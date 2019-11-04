package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.AccessRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by tommackenzie on 4/15/15.
 */
@Repository
public interface AccessRequestMapper {
    void insert(@Param("accessRequest") AccessRequest accessRequest);
}
