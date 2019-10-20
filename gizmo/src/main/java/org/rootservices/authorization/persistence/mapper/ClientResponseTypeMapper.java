package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.ClientResponseType;

/**
 * Created by tommackenzie on 8/9/16.
 */
public interface ClientResponseTypeMapper {
    void insert(@Param("clientResponseType") ClientResponseType clientResponseType);
}
