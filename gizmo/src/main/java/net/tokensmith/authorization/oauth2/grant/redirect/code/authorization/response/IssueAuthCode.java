package net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response;

import net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response.exception.AuthCodeInsertException;
import net.tokensmith.authorization.persistence.entity.*;
import net.tokensmith.authorization.persistence.repository.AccessRequestRepository;
import net.tokensmith.authorization.persistence.repository.AccessRequestScopesRepository;
import net.tokensmith.authorization.persistence.repository.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/23/15.
 */
@Component
public class IssueAuthCode {
    private AccessRequestRepository accessRequestRepository;
    private ScopeRepository scopeRepository;
    private AccessRequestScopesRepository accessRequestScopesRepository;
    private InsertAuthCodeWithRetry insertAuthCodeWithRetry;

    public IssueAuthCode() {}

    @Autowired
    public IssueAuthCode(AccessRequestRepository accessRequestRepository, ScopeRepository scopeRepository, AccessRequestScopesRepository accessRequestScopesRepository, InsertAuthCodeWithRetry insertAuthCodeWithRetry) {
        this.accessRequestRepository = accessRequestRepository;
        this.scopeRepository = scopeRepository;
        this.accessRequestRepository = accessRequestRepository;
        this.accessRequestScopesRepository = accessRequestScopesRepository;
        this.insertAuthCodeWithRetry = insertAuthCodeWithRetry;
    }

    public String run(UUID resourceOwnerId, UUID ClientUUID, Optional<URI> redirectURI, List<String> scopeNames) throws AuthCodeInsertException {

        AccessRequest accessRequest = new AccessRequest(
                UUID.randomUUID(), resourceOwnerId, ClientUUID, redirectURI
        );
        accessRequestRepository.insert(accessRequest);

        // add scopes to access request.
        if (scopeNames.size() > 0 ) {
            List<Scope> scopes = scopeRepository.findByNames(scopeNames);
            for (Scope scope : scopes) {
                AccessRequestScope accessRequestScope = new AccessRequestScope(
                        UUID.randomUUID(), accessRequest.getId(), scope
                );
                accessRequestScopesRepository.insert(accessRequestScope);
            }
        }

        String authorizationCode = insertAuthCodeWithRetry.run(accessRequest);
        return authorizationCode;
    }
}
