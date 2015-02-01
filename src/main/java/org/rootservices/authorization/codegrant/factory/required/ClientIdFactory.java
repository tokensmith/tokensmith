package org.rootservices.authorization.codegrant.factory.required;

import org.rootservices.authorization.codegrant.factory.exception.DataTypeException;
import org.rootservices.authorization.codegrant.validator.exception.EmptyValueError;
import org.rootservices.authorization.codegrant.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.codegrant.validator.exception.NoItemsError;
import org.rootservices.authorization.codegrant.validator.exception.ParamIsNullError;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface ClientIdFactory {
    public UUID makeClientId(List<String> clientIds) throws EmptyValueError, MoreThanOneItemError, NoItemsError, ParamIsNullError, DataTypeException;
}
