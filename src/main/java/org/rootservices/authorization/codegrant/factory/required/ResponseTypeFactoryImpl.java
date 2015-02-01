package org.rootservices.authorization.codegrant.factory.required;

import org.rootservices.authorization.codegrant.factory.constants.ValidationMessage;
import org.rootservices.authorization.codegrant.factory.exception.ResponseTypeException;
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

    public ResponseType makeResponseType(List<String> responseTypes) throws ResponseTypeException {

        try {
            requiredParam.run(responseTypes);
        } catch (EmptyValueError e) {
            throw new ResponseTypeException(ValidationMessage.EMPTY_VALUE.toString(), e);
        } catch (MoreThanOneItemError e) {
            throw new ResponseTypeException(ValidationMessage.MORE_THAN_ONE_ITEM.toString(), e);
        } catch (NoItemsError e) {
            throw new ResponseTypeException(ValidationMessage.EMPTY_LIST.toString(), e);
        } catch (ParamIsNullError e) {
            throw new ResponseTypeException(ValidationMessage.NULL.toString(), e);
        }

        ResponseType rt;
        try {
            rt = ResponseType.valueOf(responseTypes.get(0).toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseTypeException("Cannot coerce String to ResponseType", e);
        }

        return rt;
    }
}
