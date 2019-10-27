package helper.fixture.persistence;

import helper.fixture.FixtureFactory;
import net.tokensmith.authorization.persistence.entity.*;
import net.tokensmith.authorization.persistence.exceptions.DuplicateRecordException;
import net.tokensmith.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 *
 * Loads a client that is ready for a token to be persisted.
 */
@Component
public class LoadConfClientTokenReady {
    private LoadCodeClientWithScopes loadCodeClientWithScopes;
    private ConfidentialClientRepository confidentialClientRepository;
    private ResourceOwnerRepository resourceOwnerRepository;
    private AuthCodeRepository authCodeRepository;
    private AccessRequestRepository accessRequestRepository;
    private AccessRequestScopesRepository accessRequestScopesRepository;

    @Autowired
    public LoadConfClientTokenReady(LoadCodeClientWithScopes loadCodeClientWithScopes, ConfidentialClientRepository confidentialClientRepository, ResourceOwnerRepository resourceOwnerRepository, AuthCodeRepository authCodeRepository, AccessRequestRepository accessRequestRepository, AccessRequestScopesRepository accessRequestScopesRepository) {
        this.loadCodeClientWithScopes = loadCodeClientWithScopes;
        this.confidentialClientRepository = confidentialClientRepository;
        this.resourceOwnerRepository = resourceOwnerRepository;
        this.authCodeRepository = authCodeRepository;
        this.accessRequestRepository = accessRequestRepository;
        this.accessRequestScopesRepository = accessRequestScopesRepository;
    }

    public AuthCode run(boolean redirectUriIsPresent, boolean isRevoked, String plainTextAuthCode) throws URISyntaxException, DuplicateRecordException {
        Client client = loadCodeClientWithScopes.run();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);
        confidentialClientRepository.insert(confidentialClient);
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerRepository.insert(ro);

        AccessRequest accessRequest = FixtureFactory.makeAccessRequest(
                ro.getId(), client.getId()
        );

        if (!redirectUriIsPresent) {
            accessRequest.setRedirectURI(Optional.<URI>empty());
        }

        accessRequestRepository.insert(accessRequest);
        accessRequest.setAccessRequestScopes(new ArrayList<>());

        // make access request scopes match client.
        for (Scope scope: client.getScopes()) {
            AccessRequestScope ars = new AccessRequestScope(
                    UUID.randomUUID(),
                    accessRequest.getId(),
                    scope
            );
            accessRequestScopesRepository.insert(ars);
            accessRequest.getAccessRequestScopes().add(ars);
        }

        AuthCode authCode = FixtureFactory.makeAuthCode(accessRequest, isRevoked, plainTextAuthCode);
        authCodeRepository.insert(authCode);

        authCode.setAccessRequest(accessRequest);

        return authCode;
    }

    public void setLoadCodeClientWithScopes(LoadCodeClientWithScopes loadCodeClientWithScopes) {
        this.loadCodeClientWithScopes = loadCodeClientWithScopes;
    }
}
