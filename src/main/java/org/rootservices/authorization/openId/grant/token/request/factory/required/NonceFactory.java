package org.rootservices.authorization.openId.grant.token.request.factory.required;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.RequiredParam;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.EmptyValueError;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.NoItemsError;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.ParamIsNullError;
import org.rootservices.authorization.openId.grant.token.request.factory.exception.NonceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tommackenzie on 7/21/16.
 */
@Component
public class NonceFactory {

    private RequiredParam requiredParam;

    @Autowired
    public NonceFactory(RequiredParam requiredParam) {
        this.requiredParam = requiredParam;
    }

    public String makeNonce(List<String> nonces) throws NonceException {

        try {
            requiredParam.run(nonces);
        } catch (EmptyValueError e) {
            throw new NonceException(ErrorCode.NONCE_EMPTY_VALUE, e);
        } catch (MoreThanOneItemError e) {
            throw new NonceException(ErrorCode.NONCE_MORE_THAN_ONE_ITEM, e);
        } catch (NoItemsError e) {
            throw new NonceException(ErrorCode.NONCE_EMPTY_LIST, e);
        } catch (ParamIsNullError e) {
            throw new NonceException(ErrorCode.NONCE_NULL, e);
        }

        return nonces.get(0);
    }
}
