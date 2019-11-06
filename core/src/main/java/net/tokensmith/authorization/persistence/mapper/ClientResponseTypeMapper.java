package net.tokensmith.authorization.persistence.mapper;

import net.tokensmith.repository.entity.ClientResponseType;
import org.apache.ibatis.annotations.Param;

/**
 * Created by tommackenzie on 8/9/16.
 */
public interface ClientResponseTypeMapper {
    void insert(@Param("clientResponseType") ClientResponseType clientResponseType);
}
