package org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.optional;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.exception.ScopesException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.validator.OptionalParam;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.validator.exception.EmptyValueError;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.validator.exception.MoreThanOneItemError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tommackenzie on 1/31/15.
 */
@Component
public class ScopesBuilderImpl implements ScopesBuilder {

    @Autowired
    OptionalParam optionalParam;

    public ScopesBuilderImpl() {}

    public ScopesBuilderImpl(OptionalParam optionalParam) {
        this.optionalParam = optionalParam;
    }

    public List<String> makeScopes(List<String> items) throws ScopesException {

        try {
            optionalParam.run(items);
        } catch (EmptyValueError e) {
            throw new ScopesException(ErrorCode.SCOPES_EMPTY_VALUE, "invalid_scope", e);
        } catch (MoreThanOneItemError e) {
            throw new ScopesException(ErrorCode.SCOPES_MORE_THAN_ONE_ITEM, "invalid_request", e);
        }

        List<String> scopes;
        if ( items == null || items.isEmpty()) {
            scopes = new ArrayList<>();
        } else {
            scopes = StringToList(items);
        }
        return scopes;
    }

    private List<String> StringToList(List<String> items) throws ScopesException {
        List<String> scopes = new ArrayList<>();
        for(String item: items.get(0).split(" ")) {
            scopes.add(item);
        }
        return scopes;
    }
}
