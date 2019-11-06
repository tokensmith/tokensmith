package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.RefreshToken;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface RefreshTokenMapper {
    void insert(@Param("refreshToken") RefreshToken refreshToken);
    RefreshToken getByTokenId(@Param("tokenId") UUID tokenId);
    RefreshToken getByClientIdAndAccessToken(@Param("clientId") UUID clientId, @Param("accessToken") String accessToken);
    List<RefreshToken> getByResourceOwner(@Param("resourceOwnerId") UUID resourceOwnerId);
    void revokeByAuthCodeId(@Param("authCodeId") UUID authCodeId);
    void revokeByTokenId(@Param("tokenId") UUID tokenId);
    void revokeActive(@Param("resourceOwnerId") UUID resourceOwnerId);

}
