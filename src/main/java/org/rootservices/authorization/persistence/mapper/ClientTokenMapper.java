package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.ClientToken;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/2/16.
 */
@Repository
public interface ClientTokenMapper {
    void insert(@Param("clientToken") ClientToken clientToken);
    ClientToken getByTokenId(@Param("tokenId") UUID tokenId);
}
