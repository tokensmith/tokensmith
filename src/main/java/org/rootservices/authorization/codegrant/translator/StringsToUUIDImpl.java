package org.rootservices.authorization.codegrant.translator;

import org.rootservices.authorization.codegrant.translator.exception.ValidationError;
import org.rootservices.authorization.codegrant.validator.HasOneItem;
import org.rootservices.authorization.codegrant.validator.IsNotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 12/13/14.
 */
@Component
public class StringsToUUIDImpl implements StringsToUUID {

    @Autowired
    private HasOneItem hasOneItem;

    @Autowired
    private IsNotNull isNotNull;

    public StringsToUUIDImpl() {}

    public StringsToUUIDImpl(IsNotNull isNotNull, HasOneItem hasOneItem) {
        this.isNotNull = isNotNull;
        this.hasOneItem = hasOneItem;
    }

    public UUID run(List<String> items) throws ValidationError {

        if(isNotNull.run(items) == false) {
            throw new ValidationError("parameter is null");
        }

        if(hasOneItem.run(items) == false) {
            throw new ValidationError("parameter does not have one item");
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(items.get(0));
        } catch (IllegalArgumentException e) {
            throw new ValidationError("parameter is not UUID");
        }

        return uuid;
    }
}
