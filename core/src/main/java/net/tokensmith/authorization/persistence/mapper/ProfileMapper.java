package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.Profile;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

/**
 * Created by tommackenzie on 2/25/16.
 */
public interface ProfileMapper {
    void insert(@Param("profile") Profile profile);
    void update(@Param("resourceOwnerId") UUID resourceOwnerId, @Param("profile") Profile profile);
    Profile getById(@Param("id") UUID id);
    Profile getByResourceId(@Param("resourceOwnerId") UUID resourceOwnerId);
}
