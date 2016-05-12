package org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.validator;

import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.validator.exception.EmptyValueError;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.validator.exception.NoItemsError;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.validator.exception.ParamIsNullError;

import java.util.List;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface RequiredParam {
    public boolean run(List<String> items) throws EmptyValueError, MoreThanOneItemError, NoItemsError, ParamIsNullError;
}
