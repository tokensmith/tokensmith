package org.rootservices.authorization.codegrant.translator;

import org.rootservices.authorization.codegrant.translator.exception.ValidationError;
import org.rootservices.authorization.codegrant.validator.HasOneItem;
import org.rootservices.authorization.codegrant.validator.IsNotNull;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tommackenzie on 12/13/14.
 */
@Component
public class StringsToResponseTypeImpl implements StringsToResponseType {

    @Autowired
    private HasOneItem hasOneItem;

    @Autowired
    private IsNotNull isNotNull;

    public StringsToResponseTypeImpl() {}

    public StringsToResponseTypeImpl(IsNotNull isNotNull, HasOneItem hasOneItem) {
        this.isNotNull = isNotNull;
        this.hasOneItem = hasOneItem;
    }

    public ResponseType run(List<String> items) throws ValidationError {

        if(isNotNull.run(items) == false) {
            throw new ValidationError("parameter is null");
        }

        if(hasOneItem.run(items) == false) {
            throw new ValidationError("parameter does not have one item");
        }

        ResponseType rt;
        try {
            rt = ResponseType.valueOf(items.get(0).toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationError("parameter is not a response type");
        }

        return rt;
    }
}
