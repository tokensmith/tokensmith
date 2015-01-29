package org.rootservices.authorization.codegrant.validator;

import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 12/13/14.
 */
@Component
public class IsNotNullImpl implements IsNotNull {

    public boolean run(Object o) {
        return o != null;
    }
}
