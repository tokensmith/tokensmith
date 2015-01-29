package org.rootservices.authorization.codegrant.translator;

import org.rootservices.authorization.codegrant.translator.exception.ValidationError;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.util.List;

/**
 * Created by tommackenzie on 12/13/14.
 */
public interface StringsToResponseType {
    public ResponseType run(List<String> items) throws ValidationError;
}
