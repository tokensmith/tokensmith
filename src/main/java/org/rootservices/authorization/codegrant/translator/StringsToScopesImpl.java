package org.rootservices.authorization.codegrant.translator;

import org.rootservices.authorization.codegrant.translator.exception.EmptyValueError;
import org.rootservices.authorization.codegrant.translator.exception.InvalidValueError;
import org.rootservices.authorization.codegrant.translator.exception.ValidationError;
import org.rootservices.authorization.codegrant.validator.HasOneItem;
import org.rootservices.authorization.persistence.entity.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 1/17/15.
 */
@Component
public class StringsToScopesImpl implements StringsToScopes {

    @Autowired
    private HasOneItem hasOneItem;

    public StringsToScopesImpl() {}

    public StringsToScopesImpl(HasOneItem hasOneItem) {
        this.hasOneItem = hasOneItem;
    }

    @Override
    public List<Scope> run(List<String> items) throws ValidationError {

        // optional parameter.
        if( items.size() == 0 ) {
            return null;
        }

        if(items.get(0).isEmpty()) {
            throw new EmptyValueError("parameter is empty");
        }

        if(items.size() > 1) {
            throw new ValidationError("parameter has more than one item");
        }

        List<Scope> scopes = new ArrayList<>();
        for(String item: items.get(0).split(" "))
            try {
                scopes.add(Scope.valueOf(item.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new InvalidValueError("parameter is not a scope");
            }

        return scopes;
    }
}
