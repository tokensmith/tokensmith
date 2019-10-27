package net.tokensmith.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import net.tokensmith.authorization.persistence.entity.Scope;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by tommackenzie on 5/12/15.
 */
@Repository
public interface ScopeMapper {
    void insert(@Param("scope") Scope scope);
    List<Scope> findByNames(@Param("names") List<String> names);
    Scope findByName(@Param("name") String name);
}
