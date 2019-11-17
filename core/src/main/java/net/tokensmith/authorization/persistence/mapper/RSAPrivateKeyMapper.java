package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.RSAPrivateKeyBytes;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 2/1/16.
 */
public interface RSAPrivateKeyMapper {
    void insert(@Param("rsaPrivateKey") RSAPrivateKeyBytes rsaPrivateKey);
    void insertWithDateTimeValues(@Param("rsaPrivateKey") RSAPrivateKeyBytes rsaPrivateKey);
    RSAPrivateKeyBytes getMostRecentAndActiveForSigning();
    RSAPrivateKeyBytes getById(@Param("id") UUID id);
    List<RSAPrivateKeyBytes> getWhereActiveAndUseIsSign(@Param("limit") Integer limit, @Param("offset") Integer offset);
    RSAPrivateKeyBytes getByIdActiveSign(@Param("id") UUID id);
}
