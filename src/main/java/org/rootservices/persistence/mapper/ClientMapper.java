package org.rootservices.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.persistence.entity.AuthUser;
import org.rootservices.persistence.entity.Client;

import java.util.UUID;

/**
 * Created by tommackenzie on 11/15/14.
 */
public interface ClientMapper {
    public Client getByUUID(@Param("uuid") UUID uuid);
    public void insert(@Param("client") Client client);
}

