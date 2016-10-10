package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.Profile;
import org.rootservices.authorization.persistence.entity.RefreshToken;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/2/16.
 */
@Repository
public interface RefreshTokenMapper {
    void insert(@Param("refreshToken") RefreshToken refreshToken);
    RefreshToken getByTokenId(@Param("tokenId") UUID tokenId) throws RecordNotFoundException;
    RefreshToken getByAccessToken(@Param("accessToken") String accessToken) throws RecordNotFoundException;
    void revokeByAuthCodeId(@Param("authCodeId") UUID authCodeId);
    void revokeByTokenId(@Param("tokenId") UUID tokenId);
}
