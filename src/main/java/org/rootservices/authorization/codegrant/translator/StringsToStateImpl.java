package org.rootservices.authorization.codegrant.translator;

import org.rootservices.authorization.codegrant.translator.exception.ValidationError;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 12/27/14.
 */
@Component
public class StringsToStateImpl implements StringsToState {

    public StringsToStateImpl() {}

    @Override
    public String run(List<String> items) throws ValidationError {

        // optional parameter.
        if( items.size() == 0 ) {
            return null;
        }

        if(items.size() > 1) {
            throw new ValidationError("parameter has more than one item");
        }

        if(items.get(0).isEmpty()) {
            throw new ValidationError("parameter is empty");
        }

        return items.get(0);
    }
}
