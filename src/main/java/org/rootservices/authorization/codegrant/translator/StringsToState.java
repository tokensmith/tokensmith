package org.rootservices.authorization.codegrant.translator;

import org.rootservices.authorization.codegrant.translator.exception.ValidationError;

import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 12/27/14.
 */
public interface StringsToState {
    public String run(List<String> items) throws ValidationError;
}
