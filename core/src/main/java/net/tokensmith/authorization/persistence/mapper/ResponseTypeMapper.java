package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.ResponseType;
import org.apache.ibatis.annotations.Param;

/**
 * Created by tommackenzie on 8/9/16.
 */
public interface ResponseTypeMapper {
    ResponseType getByName(@Param("name") String name);
}
