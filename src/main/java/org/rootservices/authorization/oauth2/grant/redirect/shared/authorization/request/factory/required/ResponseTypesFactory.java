package org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.required;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.ResponseTypeException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.ScopesException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.RequiredParam;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.EmptyValueError;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.NoItemsError;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.ParamIsNullError;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by tommackenzie on 1/31/15.
 */
@Component
public class ResponseTypesFactory {

    @Autowired
    RequiredParam requiredParam;

    private static List<String> SUPPORTED_RESPONSE_TYPES = Stream.of("CODE", "TOKEN", "ID_TOKEN").collect(Collectors.toList());

    public ResponseTypesFactory() {}

    public ResponseTypesFactory(RequiredParam requiredParam) {
        this.requiredParam = requiredParam;
    }

    public List<String> makeResponseTypes(List<String> items) throws ResponseTypeException {

        try {
            requiredParam.run(items);
        } catch (EmptyValueError e) {
            throw new ResponseTypeException(ErrorCode.RESPONSE_TYPE_EMPTY_VALUE, "invalid_request", e);
        } catch (MoreThanOneItemError e) {
            throw new ResponseTypeException(ErrorCode.RESPONSE_TYPE_MORE_THAN_ONE_ITEM, "invalid_request", e);
        } catch (NoItemsError e) {
            throw new ResponseTypeException(ErrorCode.RESPONSE_TYPE_EMPTY_LIST, "invalid_request", e);
        } catch (ParamIsNullError e) {
            throw new ResponseTypeException(ErrorCode.RESPONSE_TYPE_NULL, "invalid_request", e);
        }

        List<String> responseTypes = StringToList(items);
        return responseTypes;
    }

    private List<String> StringToList(List<String> items) throws ResponseTypeException {
        List<String> scopes = new ArrayList<>();
        for(String item: items.get(0).split(" ")) {
            if(SUPPORTED_RESPONSE_TYPES.contains(item.toUpperCase())) {
                scopes.add(item.toUpperCase());
            } else {
                throw new ResponseTypeException(ErrorCode.RESPONSE_TYPE_DATA_TYPE, "unsupported_response_type");
            }
        }
        return scopes;
    }
}
