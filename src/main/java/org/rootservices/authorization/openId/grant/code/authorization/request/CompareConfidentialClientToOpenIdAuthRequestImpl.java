package org.rootservices.authorization.openId.grant.code.authorization.request;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.code.authorization.request.entity.OpenIdAuthRequest;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ConfidentialClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tommackenzie on 9/30/15.
 */
@Component
public class CompareConfidentialClientToOpenIdAuthRequestImpl implements CompareConfidentialClientToOpenIdAuthRequest {
    private ConfidentialClientRepository confidentialClientRepository;

    @Autowired
    public CompareConfidentialClientToOpenIdAuthRequestImpl(ConfidentialClientRepository confidentialClientRepository) {
        this.confidentialClientRepository = confidentialClientRepository;
    }

    @Override
    public boolean run(OpenIdAuthRequest authRequest) throws InformResourceOwnerException, InformClientException {

        ConfidentialClient confidentialClient;
        try {
            confidentialClient = confidentialClientRepository.getByClientId(authRequest.getClientId());
        } catch (RecordNotFoundException e) {
            throw new InformResourceOwnerException("The Client was not found", e, ErrorCode.CLIENT_NOT_FOUND.getCode());
        }

        if ( !confidentialClient.getClient().getRedirectURI().equals(authRequest.getRedirectURI()) ) {
            throw new InformResourceOwnerException(
                    "Redirect URI requested doesn't match client's redirect uri",
                    ErrorCode.REDIRECT_URI_MISMATCH.getCode()
            );
        }

        if ( confidentialClient.getClient().getResponseType() != authRequest.getResponseType() ) {
            throw new InformClientException(
                    "Response Type requested doesn't match client's response type",
                    "unauthorized_client",
                    ErrorCode.RESPONSE_TYPE_MISMATCH.getCode(),
                    confidentialClient.getClient().getRedirectURI()
            );
        }

        if (! hasScopes(authRequest.getScopes(), confidentialClient.getClient().getScopes())) {
            throw new InformClientException(
                    "Scope is not supported for this client.",
                    "invalid_scope",
                    ErrorCode.SCOPES_NOT_SUPPORTED.getCode(),
                    confidentialClient.getClient().getRedirectURI()
            );
        }

        return true;
    }

    private boolean hasScopes(List<String> requestedScopes, List<Scope> clientScopes) {
        boolean hasScopes = true;
        for(String scope: requestedScopes) {
            if (! clientScopes.stream().filter(o -> o.getName().equals(scope)).findFirst().isPresent()) {
                hasScopes = false;
                break;
            }
        }
        return hasScopes;
    }
}
