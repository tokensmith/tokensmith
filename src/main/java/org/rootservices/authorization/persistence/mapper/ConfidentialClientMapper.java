package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;

import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 */
public interface ConfidentialClientMapper {
    void insert(@Param("confidentialClient") ConfidentialClient confidentialClient);
    ConfidentialClient getByClientUUID(@Param("clientUUID") UUID clientUUID);
}
