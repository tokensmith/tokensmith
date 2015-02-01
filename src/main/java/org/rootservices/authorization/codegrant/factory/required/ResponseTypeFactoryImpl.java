package org.rootservices.authorization.codegrant.factory.required;

import org.rootservices.authorization.codegrant.factory.exception.DataTypeException;
import org.rootservices.authorization.codegrant.validator.RequiredParam;
import org.rootservices.authorization.codegrant.validator.exception.EmptyValueError;
import org.rootservices.authorization.codegrant.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.codegrant.validator.exception.NoItemsError;
import org.rootservices.authorization.codegrant.validator.exception.ParamIsNullError;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tommackenzie on 1/31/15.
 */
@Component
public class ResponseTypeFactoryImpl implements ResponseTypeFactory {

    @Autowired
    RequiredParam requiredParam;

    public ResponseTypeFactoryImpl() {}

    public ResponseTypeFactoryImpl(RequiredParam requiredParam) {
        this.requiredParam = requiredParam;
    }

    public ResponseType makeResponseType(List<String> responseTypes) throws EmptyValueError, MoreThanOneItemError, NoItemsError, ParamIsNullError, DataTypeException {
        requiredParam.run(responseTypes);

        ResponseType rt;
        try {
            rt = ResponseType.valueOf(responseTypes.get(0).toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DataTypeException("parameter is not a response type");
        }

        return rt;
    }
}
