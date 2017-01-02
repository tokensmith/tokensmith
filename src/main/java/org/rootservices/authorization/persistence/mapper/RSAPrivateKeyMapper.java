package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 2/1/16.
 */
public interface RSAPrivateKeyMapper {
    void insert(@Param("rsaPrivateKey") RSAPrivateKey rsaPrivateKey);
    void insertWithDateTimeValues(@Param("rsaPrivateKey") RSAPrivateKey rsaPrivateKey);
    RSAPrivateKey getMostRecentAndActiveForSigning();
    RSAPrivateKey getById(@Param("id") UUID id);
    List<RSAPrivateKey> getWhereActiveAndUseIsSign(@Param("limit") Integer limit, @Param("offset") Integer offset);
}
