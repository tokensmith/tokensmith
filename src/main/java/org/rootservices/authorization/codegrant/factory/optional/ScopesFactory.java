package org.rootservices.authorization.codegrant.factory.optional;

import org.rootservices.authorization.codegrant.factory.exception.ScopesException;
import org.rootservices.authorization.persistence.entity.Scope;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface ScopesFactory {
    public List<Scope> makeScopes(List<String> items) throws ScopesException;
}
