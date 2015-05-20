package org.rootservices.authorization.grant.code.authenticate;

import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.entity.AccessRequestScope;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.repository.AccessRequestRepository;
import org.rootservices.authorization.persistence.repository.AccessRequestScopesRepository;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.persistence.repository.ScopeRepository;
import org.rootservices.authorization.security.RandomString;
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
public class GrantAuthCodeImpl implements GrantAuthCode {
    private static final int SECONDS_TO_EXPIRATION = 120;
    @Autowired
    private RandomString randomString;
    @Autowired
    private MakeAuthCode makeAuthCode;
    @Autowired
    private AuthCodeRepository authCodeRepository;
    @Autowired
    private AccessRequestRepository accessRequestRepository;
    // additions.
    @Autowired
    private ScopeRepository scopeRepository;
    @Autowired
    private AccessRequestScopesRepository accessRequestScopesRepository;

    public GrantAuthCodeImpl() {}

    public GrantAuthCodeImpl(RandomString randomString, MakeAuthCode makeAuthCode, AuthCodeRepository authCodeRepository, AccessRequestRepository accessRequestRepository, ScopeRepository scopeRepository, AccessRequestScopesRepository accessRequestScopesRepository) {
        this.randomString = randomString;
        this.makeAuthCode = makeAuthCode;
        this.authCodeRepository = authCodeRepository;
        this.accessRequestRepository = accessRequestRepository;
        this.scopeRepository = scopeRepository;
        this.accessRequestRepository = accessRequestRepository;
        this.accessRequestScopesRepository = accessRequestScopesRepository;
    }

    public String run(UUID resourceOwnerUUID, UUID ClientUUID, Optional<URI> redirectURI, List<String> scopeNames) {

        String authorizationCode = randomString.run();
        AuthCode authCode = makeAuthCode.run(
                resourceOwnerUUID,
                ClientUUID,
                authorizationCode,
                SECONDS_TO_EXPIRATION
        );
        authCodeRepository.insert(authCode);

        AccessRequest accessRequest = new AccessRequest(
                UUID.randomUUID(), redirectURI, authCode.getUuid()
        );
        accessRequestRepository.insert(accessRequest);

        // link access request to scopes.
        if (scopeNames.size() > 0 ) {
            List<Scope> scopes = scopeRepository.findByName(scopeNames);
            for (Scope scope : scopes) {
                AccessRequestScope accessRequestScope = new AccessRequestScope(
                        UUID.randomUUID(), accessRequest.getUuid(), scope.getUuid()
                );
                accessRequestScopesRepository.insert(accessRequestScope);
            }
        }

        return authorizationCode;
    }

    public int getSecondsToExpiration() {
        return SECONDS_TO_EXPIRATION;
    }

}
