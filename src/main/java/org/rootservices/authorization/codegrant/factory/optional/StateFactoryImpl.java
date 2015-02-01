package org.rootservices.authorization.codegrant.factory.optional;

import org.rootservices.authorization.codegrant.validator.OptionalParam;
import org.rootservices.authorization.codegrant.validator.exception.EmptyValueError;
import org.rootservices.authorization.codegrant.validator.exception.MoreThanOneItemError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 1/31/15.
 */
@Component
public class StateFactoryImpl implements StateFactory {

    @Autowired
    OptionalParam optionalParam;

    public StateFactoryImpl() {}

    public StateFactoryImpl(OptionalParam optionalParam) {
        this.optionalParam = optionalParam;
    }

    public Optional<String> makeState(List<String> states) throws EmptyValueError, MoreThanOneItemError {
        optionalParam.run(states);

        String state;
        if( states == null) {
            state = null;
        } else {
            state = states.get(0);
        }

        return Optional.ofNullable(state);

    }
}
