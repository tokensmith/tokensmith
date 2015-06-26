package org.rootservices.authorization.grant.code.protocol.authorization.request.factory.required;

import org.rootservices.authorization.grant.code.protocol.authorization.request.factory.exception.ResponseTypeException;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.util.List;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface ResponseTypeFactory {
    public ResponseType makeResponseType(List<String> responseTypes) throws ResponseTypeException;
}
