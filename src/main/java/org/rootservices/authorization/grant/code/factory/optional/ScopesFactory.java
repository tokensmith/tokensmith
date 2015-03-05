package org.rootservices.authorization.grant.code.factory.optional;

import org.rootservices.authorization.grant.code.factory.exception.ScopesException;
import org.rootservices.authorization.persistence.entity.Scope;

import java.util.List;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface ScopesFactory {
    public List<Scope> makeScopes(List<String> items) throws ScopesException;
}
