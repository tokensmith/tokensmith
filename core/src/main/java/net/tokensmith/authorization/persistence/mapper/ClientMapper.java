package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.Client;
import org.apache.ibatis.annotations.Param;
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

