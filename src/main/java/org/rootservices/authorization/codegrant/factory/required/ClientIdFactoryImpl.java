package org.rootservices.authorization.codegrant.factory.required;

import org.rootservices.authorization.codegrant.factory.exception.DataTypeException;
import org.rootservices.authorization.codegrant.validator.RequiredParam;
import org.rootservices.authorization.codegrant.validator.exception.EmptyValueError;
import org.rootservices.authorization.codegrant.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.codegrant.validator.exception.NoItemsError;
import org.rootservices.authorization.codegrant.validator.exception.ParamIsNullError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 1/31/15.
 */
@Component
public class ClientIdFactoryImpl implements ClientIdFactory {

    @Autowired
    RequiredParam requiredParam;

    public ClientIdFactoryImpl() {}

    public ClientIdFactoryImpl(RequiredParam requiredParam) {
        this.requiredParam = requiredParam;
    }

    public UUID makeClientId(List<String> items) throws EmptyValueError, MoreThanOneItemError, NoItemsError, ParamIsNullError, DataTypeException {
        requiredParam.run(items);

        UUID uuid;
        try {
            uuid = UUID.fromString(items.get(0));
        } catch (IllegalArgumentException e) {
            throw new DataTypeException("Cannot coerce string to UUID");
        }

        return uuid;
    }
}
