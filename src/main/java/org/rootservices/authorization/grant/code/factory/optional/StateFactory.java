package org.rootservices.authorization.grant.code.factory.optional;

import org.rootservices.authorization.grant.code.factory.exception.StateException;

import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface StateFactory {
    public Optional<String> makeState(List<String> states) throws StateException;
}
