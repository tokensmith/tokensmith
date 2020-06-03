package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.Address;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

/**
 * Created by tommackenzie on 2/25/16.
 */
public interface AddressMapper {
    void insert(@Param("address") Address address);
    Address getByIdAndResourceOwnerId(@Param("id") UUID id, @Param("resourceOwnerId") UUID resourceOwnerId);
    void update(@Param("resourceOwnerId") UUID resourceOwnerId, @Param("address") Address address);
    void delete(@Param("id") UUID id, @Param("resourceOwnerId") UUID resourceOwnerId);
}
