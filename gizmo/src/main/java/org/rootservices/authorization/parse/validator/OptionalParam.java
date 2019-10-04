package org.rootservices.authorization.parse.validator;


import org.rootservices.authorization.parse.validator.excpeption.EmptyValueError;
import org.rootservices.authorization.parse.validator.excpeption.MoreThanOneItemError;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class OptionalParam {
    private static String EMPTY= "parameter is empty";
    private static String TOO_MANY_VALUES= "parameter is empty";

    /**
     * Determines if the items passes the rules for a optional field. Which are:
     *  - items first element cannot be empty (implies an empty value, it must have a non empty value)
     *  - items cannot have more than one value (per OAUTH2)
     *
     * @param items
     * @return true if passes. raises exception if fails.
     * @throws EmptyValueError
     * @throws MoreThanOneItemError
     */
    public boolean run(List<String> items) throws EmptyValueError, MoreThanOneItemError {

        // optional parameter.
        if( items == null || items.size() == 0 ) {
            return true;
        }

        if(items.get(0).isEmpty()) {
            throw new EmptyValueError(EMPTY);
        }

        if(items.size() > 1) {
            throw new MoreThanOneItemError(TOO_MANY_VALUES);
        }

        return true;
    }
}
