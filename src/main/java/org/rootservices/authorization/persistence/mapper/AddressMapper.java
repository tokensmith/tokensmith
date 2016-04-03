package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.Address;

import java.util.UUID;

/**
 * Created by tommackenzie on 2/25/16.
 */
public interface AddressMapper {
    void insert(@Param("address") Address address);
    Address getById(@Param("id") UUID id);
}
