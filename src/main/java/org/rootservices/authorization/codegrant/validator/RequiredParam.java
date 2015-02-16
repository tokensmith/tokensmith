package org.rootservices.authorization.codegrant.validator;

import org.rootservices.authorization.codegrant.validator.exception.EmptyValueError;
import org.rootservices.authorization.codegrant.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.codegrant.validator.exception.NoItemsError;
import org.rootservices.authorization.codegrant.validator.exception.ParamIsNullError;

import java.util.List;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface RequiredParam {
    public boolean run(List<String> items) throws EmptyValueError, MoreThanOneItemError, NoItemsError, ParamIsNullError;
}
