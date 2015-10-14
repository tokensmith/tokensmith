package org.rootservices.authorization.grant.openid.protocol.authorization.request;

import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.openid.protocol.authorization.request.builder.OpenIdAuthRequestBuilder;
import org.rootservices.authorization.grant.openid.protocol.authorization.request.entity.OpenIdAuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tommackenzie on 10/10/15.
 */
@Component
public class ValidateOpenIdParamsmpl implements ValidateOpenIdParams {

    private OpenIdAuthRequestBuilder openIdAuthRequestBuilder;
    private CompareClientToOpenIdAuthRequest compareClientToOpenIdAuthRequest;

    @Autowired
    public ValidateOpenIdParamsmpl(OpenIdAuthRequestBuilder openIdAuthRequestBuilder, CompareClientToOpenIdAuthRequest compareClientToOpenIdAuthRequest) {
        this.openIdAuthRequestBuilder = openIdAuthRequestBuilder;
        this.compareClientToOpenIdAuthRequest = compareClientToOpenIdAuthRequest;
    }

    @Override
    public OpenIdAuthRequest run(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states) throws InformResourceOwnerException, InformClientException {
        OpenIdAuthRequest openIdAuthRequest = openIdAuthRequestBuilder.build(clientIds, responseTypes, redirectUris, scopes, states);
        compareClientToOpenIdAuthRequest.run(openIdAuthRequest);

        return openIdAuthRequest;
    }
}
