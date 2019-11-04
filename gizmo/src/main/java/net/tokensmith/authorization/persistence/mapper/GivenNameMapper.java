package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.GivenName;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

/**
 * Created by tommackenzie on 2/25/16.
 */
public interface GivenNameMapper {
    void insert(@Param("givenName") GivenName givenName);
    GivenName findById(@Param("id") UUID id);
}
