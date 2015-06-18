package helper.fixture.persistence;

import helper.fixture.FixtureFactory;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.repository.*;

import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 *
 * Loads a client that is ready for a token to be persisted.
 */
public class LoadConfidentialClientTokenReady {
    private LoadClientWithScopes loadClientWithScopes;
    private ConfidentialClientRepository confidentialClientRepository;
    private ResourceOwnerRepository resourceOwnerRepository;
    private AuthCodeRepository authCodeRepository;
    private AccessRequestRepository accessRequestRepository;
    private AccessRequestScopesRepository accessRequestScopesRepository;

    public LoadConfidentialClientTokenReady(LoadClientWithScopes loadClientWithScopes, ConfidentialClientRepository confidentialClientRepository, ResourceOwnerRepository resourceOwnerRepository, AuthCodeRepository authCodeRepository, AccessRequestRepository accessRequestRepository, AccessRequestScopesRepository accessRequestScopesRepository) {
        this.loadClientWithScopes = loadClientWithScopes;
        this.confidentialClientRepository = confidentialClientRepository;
        this.resourceOwnerRepository = resourceOwnerRepository;
        this.authCodeRepository = authCodeRepository;
        this.accessRequestRepository = accessRequestRepository;
        this.accessRequestScopesRepository = accessRequestScopesRepository;
    }

    public AuthCode run() throws URISyntaxException {
        Client client = loadClientWithScopes.run();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);
        confidentialClientRepository.insert(confidentialClient);
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerRepository.insert(ro);

        AccessRequest accessRequest = FixtureFactory.makeAccessRequest(
                ro.getUuid(), client.getUuid(), null
        );

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

        AuthCode authCode = FixtureFactory.makeAuthCode(accessRequest);
        authCodeRepository.insert(authCode);

        authCode.setAccessRequest(accessRequest);

        return authCode;
    }
}
