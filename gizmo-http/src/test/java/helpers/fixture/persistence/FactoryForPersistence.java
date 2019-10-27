package helpers.fixture.persistence;

import helpers.fixture.persistence.http.*;
import helpers.fixture.persistence.db.GetOrCreateRSAPrivateKey;
import helpers.suite.IntegrationTestSuite;
import net.tokensmith.authorization.security.GenerateRSAPrivateKey;
import net.tokensmith.authorization.persistence.repository.*;
import net.tokensmith.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Base64;

/**
 * Created by tommackenzie on 6/5/15.
 */
public class FactoryForPersistence {
    private AnnotationConfigApplicationContext context;

    public FactoryForPersistence(AnnotationConfigApplicationContext context) {
        this.context = context;
    }

    public GetSessionAndCsrfToken makeGetSessionAndCsrfToken() {
        return new GetSessionAndCsrfToken(IntegrationTestSuite.getHttpClient());
    }

    public PostAuthorizationForm makePostAuthorizationForm() {
        return new PostAuthorizationForm(
                IntegrationTestSuite.getHttpClient(),
                makeGetSessionAndCsrfToken()
        );
    }

    public PostTokenCodeGrant makePostTokenCodeGrant() {
        AppConfig config = new AppConfig();
        return new PostTokenCodeGrant(
                IntegrationTestSuite.getHttpClient(),
                config.objectMapper()
        );
    }

    public PostTokenRefreshGrant makePostTokenRefreshGrant() {
        AppConfig config = new AppConfig();
        return new PostTokenRefreshGrant(
                IntegrationTestSuite.getHttpClient(),
                config.objectMapper()
        );
    }

    public GetOrCreateRSAPrivateKey getOrCreateRSAPrivateKey() {
        GenerateRSAPrivateKey generateRSAPrivateKey = IntegrationTestSuite.getContext().getBean(GenerateRSAPrivateKey.class);
        RsaPrivateKeyRepository rsaPrivateKeyRepository = IntegrationTestSuite.getContext().getBean(RsaPrivateKeyRepository.class);

        return new GetOrCreateRSAPrivateKey(generateRSAPrivateKey, rsaPrivateKeyRepository);
    }

    public PostTokenPasswordGrant postPasswordGrant() {
        AppConfig config = new AppConfig();
        return new PostTokenPasswordGrant(
                IntegrationTestSuite.getHttpClient(),
                config.objectMapper(),
                Base64.getEncoder()
        );
    }
}
