package org.rootservices.authorization.grant.code.protocol.authorization.request.buider.validator;

import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.validator.exception.EmptyValueError;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.validator.exception.MoreThanOneItemError;


import java.util.List;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface OptionalParam {
    boolean run(List<String> items) throws EmptyValueError, MoreThanOneItemError;
}
