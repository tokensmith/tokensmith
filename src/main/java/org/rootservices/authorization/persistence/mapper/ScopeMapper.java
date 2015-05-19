package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.Scope;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by tommackenzie on 5/12/15.
 */
@Repository
public interface ScopeMapper {
    void insert(@Param("scope") Scope scope);
    List<Scope> findByName(@Param("names") List<String> names);
}
