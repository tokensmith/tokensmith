package org.rootservices.authorization.codegrant.translator;

import org.rootservices.authorization.codegrant.translator.exception.EmptyValueError;
import org.rootservices.authorization.codegrant.translator.exception.InvalidValueError;
import org.rootservices.authorization.codegrant.translator.exception.ValidationError;

import java.net.URI;
import java.util.List;

/**
 * Created by tommackenzie on 1/22/15.
 */
public interface StringsToURI {
    public URI run(List<String> items) throws EmptyValueError, InvalidValueError, ValidationError;
}
