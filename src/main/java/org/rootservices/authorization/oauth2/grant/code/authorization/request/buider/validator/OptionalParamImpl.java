package org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.validator;

import org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.validator.exception.EmptyValueError;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.validator.exception.MoreThanOneItemError;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tommackenzie on 1/31/15.
 */
@Component
public class OptionalParamImpl implements OptionalParam {

    @Override
    public boolean run(List<String> items) throws EmptyValueError, MoreThanOneItemError {

        // optional parameter.
        if( items == null || items.size() == 0 ) {
            return true;
        }

        if(items.get(0).isEmpty()) {
            throw new EmptyValueError("parameter is empty");
        }

        if(items.size() > 1) {
            throw new MoreThanOneItemError("parameter has more than one item");
        }

        return true;
    }
}
