package org.rootservices.authorization.grant.code.protocol.authorization.builder.required;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.authorization.builder.exception.ClientIdException;
import org.rootservices.authorization.grant.code.protocol.authorization.validator.RequiredParam;
import org.rootservices.authorization.grant.code.protocol.authorization.validator.exception.EmptyValueError;
import org.rootservices.authorization.grant.code.protocol.authorization.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.grant.code.protocol.authorization.validator.exception.NoItemsError;
import org.rootservices.authorization.grant.code.protocol.authorization.validator.exception.ParamIsNullError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 1/31/15.
 */
@Component
public class ClientIdBuilderImpl implements ClientIdBuilder {

    @Autowired
    RequiredParam requiredParam;

    public ClientIdBuilderImpl() {}

    public ClientIdBuilderImpl(RequiredParam requiredParam) {
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
