package org.rootservices.authorization.codegrant.validator;

import org.rootservices.authorization.codegrant.validator.exception.EmptyValueError;
import org.rootservices.authorization.codegrant.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.codegrant.validator.exception.NoItemsError;
import org.rootservices.authorization.codegrant.validator.exception.ParamIsNullError;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tommackenzie on 1/31/15.
 */
@Component
public class RequiredParamImpl implements RequiredParam {

    @Override
    public boolean run(List<String> items) throws EmptyValueError, MoreThanOneItemError, NoItemsError, ParamIsNullError {

        if(items == null) {
            throw new ParamIsNullError("parameter is null");
        }

        if (items.isEmpty()) {
            throw new NoItemsError("parameter does not have one item");
        }

        if (items.get(0).isEmpty()) {
            throw new EmptyValueError("parameter had no value");
        }

        if(items.size() > 1) {
            throw new MoreThanOneItemError("parameter has more than one item");
        }

        return true;
    }
}
