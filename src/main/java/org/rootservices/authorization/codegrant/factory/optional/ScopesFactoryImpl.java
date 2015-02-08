package org.rootservices.authorization.codegrant.factory.optional;

import org.rootservices.authorization.codegrant.constant.ErrorCode;
import org.rootservices.authorization.codegrant.factory.exception.ScopesException;
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

    public List<Scope> makeScopes(List<String> items) throws ScopesException {

        try {
            optionalParam.run(items);
        } catch (EmptyValueError e) {
            throw new ScopesException(ErrorCode.EMPTY_VALUE, "invalid_scope", e);
        } catch (MoreThanOneItemError e) {
            throw new ScopesException(ErrorCode.MORE_THAN_ONE_ITEM, "invalid_request", e);
        }

        List<Scope> scopes;
        if ( items == null || items.isEmpty()) {
            scopes = null;
        } else {
            scopes = StringsToScopes(items);
        }
        return scopes;
    }

    private List<Scope> StringsToScopes(List<String> items) throws ScopesException {
        List<Scope> scopes = new ArrayList<>();
        for(String item: items.get(0).split(" ")) {

            Scope tmpScope;
            try {
                tmpScope = Scope.valueOf(item.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ScopesException(ErrorCode.EMPTY_VALUE, "invalid_scope", e);
            }
            scopes.add(tmpScope);
        }
        return scopes;
    }
}
