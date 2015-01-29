package org.rootservices.authorization.codegrant.translator;

import org.rootservices.authorization.codegrant.translator.exception.ValidationError;
import org.rootservices.authorization.persistence.entity.Scope;

import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 1/17/15.
 */
public interface StringsToScopes {
    public List<Scope> run(List<String> items) throws ValidationError;
}
