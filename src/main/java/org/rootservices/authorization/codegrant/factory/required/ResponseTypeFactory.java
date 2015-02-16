package org.rootservices.authorization.codegrant.factory.required;

import org.rootservices.authorization.codegrant.factory.exception.ResponseTypeException;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface ResponseTypeFactory {
    public ResponseType makeResponseType(List<String> responseTypes) throws ResponseTypeException;
}
