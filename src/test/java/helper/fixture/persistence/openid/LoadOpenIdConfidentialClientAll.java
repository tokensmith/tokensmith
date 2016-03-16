package helper.fixture.persistence.openid;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadClientWithScopes;
import helper.fixture.persistence.LoadConfidentialClientTokenReady;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.entity.Profile;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.*;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 1/24/16.
 *
 * Loads all data associated with a confidential client.
 *  - scopes (openid)
 *  - resource owner
 *  - resource owner profile
 *  - access request
 *  - access request scopes (openid)
 *  - auth code
 *  - token
 */
@Component
public class LoadOpenIdConfidentialClientAll {
    private LoadConfidentialClientTokenReady loadConfidentialClientOpendIdTokenReady;
    private ProfileRepository profileRepository;
    private RandomString randomString;
    private TokenRepository tokenRepository;

    @Autowired
    public LoadOpenIdConfidentialClientAll(LoadConfidentialClientTokenReady loadConfidentialClientOpendIdTokenReady, ProfileRepository profileRepository, RandomString randomString, TokenRepository tokenRepository){
        this.loadConfidentialClientOpendIdTokenReady = loadConfidentialClientOpendIdTokenReady;
        this.profileRepository = profileRepository;
        this.randomString = randomString;
        this.tokenRepository = tokenRepository;
    }

    public Token run() throws DuplicateRecordException, URISyntaxException {
        AuthCode authCode = loadConfidentialClientOpendIdTokenReady.run(true, false, "plain-text-auth-code");

        Token token = FixtureFactory.makeToken(authCode.getUuid());
        String accessToken = randomString.run();
        token.setToken(accessToken.getBytes());
        tokenRepository.insert(token);

        return token;
    }
}
