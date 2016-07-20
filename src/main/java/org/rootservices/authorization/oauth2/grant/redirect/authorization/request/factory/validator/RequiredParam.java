package org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.validator;

import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.validator.exception.EmptyValueError;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.validator.exception.NoItemsError;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.validator.exception.ParamIsNullError;

import java.util.List;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface RequiredParam {
    boolean run(List<String> items) throws EmptyValueError, MoreThanOneItemError, NoItemsError, ParamIsNullError;
}
