package helper.fixture.persistence.openid;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadConfidentialClientTokenReady;
import org.rootservices.authorization.persistence.entity.AuthCode;
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
 *  - access request
 *  - access request scopes (openid)
 *  - auth code
 *  - token
 */
@Component
public class LoadOpenIdConfidentialClientAll {
    private LoadConfidentialClientTokenReady loadConfidentialClientOpendIdTokenReady;
    private RandomString randomString;
    private TokenRepository tokenRepository;

    @Autowired
    public LoadOpenIdConfidentialClientAll(LoadConfidentialClientTokenReady loadConfidentialClientOpendIdTokenReady, RandomString randomString, TokenRepository tokenRepository){
        this.loadConfidentialClientOpendIdTokenReady = loadConfidentialClientOpendIdTokenReady;
        this.randomString = randomString;
        this.tokenRepository = tokenRepository;
    }

    public Token run() throws DuplicateRecordException, URISyntaxException {
        AuthCode authCode = loadConfidentialClientOpendIdTokenReady.run(true, false, "plain-text-auth-code");
        Token token = FixtureFactory.makeOpenIdToken();
        String accessToken = randomString.run();
        token.setToken(accessToken.getBytes());
        tokenRepository.insert(token);
        return token;
    }
}
