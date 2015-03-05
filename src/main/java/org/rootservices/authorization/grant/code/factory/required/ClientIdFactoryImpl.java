package org.rootservices.authorization.grant.code.factory.required;

import org.rootservices.authorization.grant.code.constant.ErrorCode;
import org.rootservices.authorization.grant.code.factory.exception.ClientIdException;
import org.rootservices.authorization.grant.code.validator.RequiredParam;
import org.rootservices.authorization.grant.code.validator.exception.EmptyValueError;
import org.rootservices.authorization.grant.code.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.grant.code.validator.exception.NoItemsError;
import org.rootservices.authorization.grant.code.validator.exception.ParamIsNullError;
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

    public UUID makeClientId(List<String> items) throws ClientIdException {

        try {
            requiredParam.run(items);
        } catch (EmptyValueError e) {
            throw new ClientIdException(ErrorCode.CLIENT_ID_EMPTY_VALUE, e);
        } catch (MoreThanOneItemError e) {
            throw new ClientIdException(ErrorCode.CLIENT_ID_MORE_THAN_ONE_ITEM, e);
        } catch (NoItemsError e) {
            throw new ClientIdException(ErrorCode.CLIENT_ID_EMPTY_LIST, e);
        } catch (ParamIsNullError e) {
            throw new ClientIdException(ErrorCode.CLIENT_ID_NULL, e);
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(items.get(0));
        } catch (IllegalArgumentException e) {
            throw new ClientIdException(ErrorCode.CLIENT_ID_DATA_TYPE, e);
        }

        return uuid;
    }

}
