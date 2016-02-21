package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;

/**
 * Created by tommackenzie on 2/1/16.
 */
public interface RSAPrivateKeyMapper {
    void insert(@Param("rsaPrivateKey") RSAPrivateKey rsaPrivateKey);
    RSAPrivateKey getMostRecentAndActiveForSigning();
}
