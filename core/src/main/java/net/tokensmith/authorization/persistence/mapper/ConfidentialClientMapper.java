package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.ConfidentialClient;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 */
public interface ConfidentialClientMapper {
    void insert(@Param("confidentialClient") ConfidentialClient confidentialClient);
    ConfidentialClient getByClientId(@Param("clientId") UUID clientId);
}
