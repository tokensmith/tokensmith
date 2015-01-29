package org.rootservices.authorization.codegrant.validator;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tommackenzie on 12/13/14.
 */
@Component
public class HasOneItemImpl implements HasOneItem {

    public boolean run(List items) {

        if (items == null || items.size() == 0 || items.size() > 1) {
            return false;
        }
        return true;
    }
}
