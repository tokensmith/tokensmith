package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.Client;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Created by tommackenzie on 11/15/14.
 */
@Repository
public interface ClientMapper {
    Client getById(@Param("id") UUID id);
    void insert(@Param("client") Client client);
}

