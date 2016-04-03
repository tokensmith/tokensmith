package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/15/15.
 */
@Repository
public interface AccessRequestMapper {
    void insert(@Param("accessRequest") AccessRequest accessRequest);
    AccessRequest getByAccessToken(@Param("accessToken") byte[] accessToken);
}
