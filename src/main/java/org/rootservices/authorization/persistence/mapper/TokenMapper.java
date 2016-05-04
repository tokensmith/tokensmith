package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.Token;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Created by tommackenzie on 5/23/15.
 */
@Repository
public interface TokenMapper {
    void insert(@Param("token") Token token) throws DuplicateKeyException;
    void revokeByAuthCodeId(@Param("authCodeId") UUID authCodeId);
    Token getByAuthCodeId(@Param("authCodeId") UUID authCodeId);
}
