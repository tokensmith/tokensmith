package org.rootservices.authorization.codegrant.factory.optional;

import org.rootservices.authorization.codegrant.factory.exception.DataTypeException;
import org.rootservices.authorization.codegrant.validator.OptionalParam;
import org.rootservices.authorization.codegrant.validator.exception.*;
import org.rootservices.authorization.persistence.entity.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tommackenzie on 1/31/15.
 */
@Component
public class ScopesFactoryImpl implements ScopesFactory {

    @Autowired
    OptionalParam optionalParam;

    public ScopesFactoryImpl() {}

    public ScopesFactoryImpl(OptionalParam optionalParam) {
        this.optionalParam = optionalParam;
    }

    public List<Scope> makeScopes(List<String> items) throws EmptyValueError, MoreThanOneItemError, DataTypeException {
        optionalParam.run(items);

        List<Scope> scopes;
        if ( items == null ) {
            scopes = null;
        } else {
            scopes = StringsToScopes(items);
        }
        return scopes;
    }

    private List<Scope> StringsToScopes(List<String> items) throws DataTypeException{
        List<Scope> scopes = new ArrayList<>();
        for(String item: items.get(0).split(" ")) {

            Scope tmpScope;
            try {
                tmpScope = Scope.valueOf(item.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new DataTypeException("parameter is not a scope");
            }
            scopes.add(tmpScope);
        }
        return scopes;
    }
}
