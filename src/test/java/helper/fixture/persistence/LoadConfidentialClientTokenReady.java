package helper.fixture.persistence;

import helper.fixture.FixtureFactory;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 *
 * Loads a client that is ready for a token to be persisted.
 */
@Component
public class LoadConfidentialClientTokenReady {
    private LoadClientWithScopes loadClientWithScopes;
    private ConfidentialClientRepository confidentialClientRepository;
    private ResourceOwnerRepository resourceOwnerRepository;
    private AuthCodeRepository authCodeRepository;
    private AccessRequestRepository accessRequestRepository;
    private AccessRequestScopesRepository accessRequestScopesRepository;

    @Autowired
    public LoadConfidentialClientTokenReady(LoadClientWithScopes loadClientWithScopes, ConfidentialClientRepository confidentialClientRepository, ResourceOwnerRepository resourceOwnerRepository, AuthCodeRepository authCodeRepository, AccessRequestRepository accessRequestRepository, AccessRequestScopesRepository accessRequestScopesRepository) {
        this.loadClientWithScopes = loadClientWithScopes;
        this.confidentialClientRepository = confidentialClientRepository;
        this.resourceOwnerRepository = resourceOwnerRepository;
        this.authCodeRepository = authCodeRepository;
        this.accessRequestRepository = accessRequestRepository;
        this.accessRequestScopesRepository = accessRequestScopesRepository;
    }

    public AuthCode run(boolean redirectUriIsPresent, boolean isRevoked, String plainTextAuthCode) throws URISyntaxException, DuplicateRecordException {
        Client client = loadClientWithScopes.run();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);
        confidentialClientRepository.insert(confidentialClient);
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerRepository.insert(ro);

        AccessRequest accessRequest = FixtureFactory.makeAccessRequest(
                ro.getUuid(), client.getUuid()
        );

        if (!redirectUriIsPresent) {
            accessRequest.setRedirectURI(Optional.<URI>empty());
        }

        accessRequestRepository.insert(accessRequest);

        // make access request scopes match client.
        for (Scope scope: client.getScopes()) {
            AccessRequestScope ars = new AccessRequestScope(
                    UUID.randomUUID(),
                    accessRequest.getUuid(),
                    scope.getUuid()
            );
            accessRequestScopesRepository.insert(ars);
        }

        AuthCode authCode = FixtureFactory.makeAuthCode(accessRequest, isRevoked, plainTextAuthCode);
        authCodeRepository.insert(authCode);

        authCode.setAccessRequest(accessRequest);

        return authCode;
    }

    public void setLoadClientWithScopes(LoadClientWithScopes loadClientWithScopes) {
        this.loadClientWithScopes = loadClientWithScopes;
    }
}
