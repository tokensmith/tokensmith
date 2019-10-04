package org.rootservices.authorization.parse.validator;

import org.rootservices.authorization.parse.validator.excpeption.EmptyValueError;
import org.rootservices.authorization.parse.validator.excpeption.MoreThanOneItemError;
import org.rootservices.authorization.parse.validator.excpeption.NoItemsError;
import org.rootservices.authorization.parse.validator.excpeption.ParamIsNullError;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class RequiredParam {
    private static String IS_NULL="parameter is null";
    private static String NO_ITEMS="parameter does not have one item";
    private static String EMPTY="parameter had no value";
    private static String TOO_MANY_VALUES="parameter has more than one item";

    /**
     * Determines if the items passes the rules for a required field. Which are:
     *  - items cannot be null
     *  - items cannot be a empty list
     *  - items first element cannot be empty
     *  - items cannot have more than one value (per OAUTH2)
     *
     * @param items
     * @return true if passes. raises exception if fails.
     * @throws EmptyValueError
     * @throws MoreThanOneItemError
     * @throws NoItemsError
     * @throws ParamIsNullError
     */
    public boolean run(List<String> items) throws EmptyValueError, MoreThanOneItemError, NoItemsError, ParamIsNullError {

        if(items == null) {
            throw new ParamIsNullError(IS_NULL);
        }

        if (items.isEmpty()) {
            throw new NoItemsError(NO_ITEMS);
        }

        if (items.get(0).isEmpty()) {
            throw new EmptyValueError(EMPTY);
        }

        if(items.size() > 1) {
            throw new MoreThanOneItemError(TOO_MANY_VALUES);
        }

        return true;
    }
}
