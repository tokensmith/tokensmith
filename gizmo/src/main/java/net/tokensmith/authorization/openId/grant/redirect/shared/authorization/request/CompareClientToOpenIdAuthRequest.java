package net.tokensmith.authorization.openId.grant.redirect.shared.authorization.request;

import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import net.tokensmith.authorization.openId.grant.redirect.shared.authorization.request.entity.BaseOpenIdAuthRequest;
import net.tokensmith.authorization.persistence.entity.Client;
import net.tokensmith.authorization.persistence.entity.ResponseType;
import net.tokensmith.authorization.persistence.entity.Scope;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 9/30/15.
 */
public abstract class CompareClientToOpenIdAuthRequest {

    public boolean run(BaseOpenIdAuthRequest authRequest) throws InformResourceOwnerException, InformClientException {

        Client client;
        try {
            client = getClient(authRequest.getClientId());
        } catch (RecordNotFoundException e) {
            throw new InformResourceOwnerException("The Client was not found", e, ErrorCode.CLIENT_NOT_FOUND.getCode());
        }

        if ( !client.getRedirectURI().equals(authRequest.getRedirectURI()) ) {
            throw new InformResourceOwnerException(
                    "Redirect URI requested doesn't match client's redirect uri",
                    ErrorCode.REDIRECT_URI_MISMATCH.getCode()
            );
        }

        if ( ! hasResponseTypes(authRequest.getResponseTypes(), client.getResponseTypes())) {
            throw new InformClientException(
                    "Response Type requested doesn't match client's response type",
                    "unauthorized_client",
                    ErrorCode.RESPONSE_TYPE_MISMATCH.getDescription(),
                    ErrorCode.RESPONSE_TYPE_MISMATCH.getCode(),
                    client.getRedirectURI(),
                    authRequest.getState()
            );
        }

        if (! hasScopes(authRequest.getScopes(), client.getScopes())) {
            throw new InformClientException(
                    "Scope is not supported for this client.",
                    "invalid_scope",
                    ErrorCode.SCOPES_NOT_SUPPORTED.getDescription(),
                    ErrorCode.SCOPES_NOT_SUPPORTED.getCode(),
                    client.getRedirectURI(),
                    authRequest.getState()
            );
        }

        return true;
    }

    private boolean hasResponseTypes(List<String> requestedResponseTypes, List<ResponseType> clientResponseTypes) {
        boolean hasScopes = true;
        for(String responseType: requestedResponseTypes) {
            if (! clientResponseTypes.stream().filter(o -> o.getName().equalsIgnoreCase(responseType)).findFirst().isPresent()) {
                hasScopes = false;
                break;
            }
        }
        return hasScopes;
    }

    private boolean hasScopes(List<String> requestedScopes, List<Scope> clientScopes) {
        boolean hasScopes = true;
        for(String scope: requestedScopes) {
            if (! clientScopes.stream().filter(o -> o.getName().equalsIgnoreCase(scope)).findFirst().isPresent()) {
                hasScopes = false;
                break;
            }
        }
        return hasScopes;
    }

    protected abstract Client getClient(UUID clientId) throws RecordNotFoundException;
}
