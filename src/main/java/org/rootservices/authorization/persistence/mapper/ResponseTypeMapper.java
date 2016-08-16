package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.ResponseType;

/**
 * Created by tommackenzie on 8/9/16.
 */
public interface ResponseTypeMapper {
    ResponseType getByName(@Param("name") String name);
}
