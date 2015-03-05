package org.rootservices.authorization.grant.code.validator;

import org.rootservices.authorization.grant.code.validator.exception.EmptyValueError;
import org.rootservices.authorization.grant.code.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.grant.code.validator.exception.NoItemsError;
import org.rootservices.authorization.grant.code.validator.exception.ParamIsNullError;

import java.util.List;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface RequiredParam {
    public boolean run(List<String> items) throws EmptyValueError, MoreThanOneItemError, NoItemsError, ParamIsNullError;
}
