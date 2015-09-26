package org.rootservices.authorization.grant.code.protocol.authorization.request.buider.optional;

import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.StateException;

import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface StateBuilder {
    Optional<String> makeState(List<String> states) throws StateException;
}
