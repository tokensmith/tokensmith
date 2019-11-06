package net.tokensmith.authorization.http.factory;


import net.tokensmith.authorization.oauth2.grant.token.entity.Extension;
import net.tokensmith.authorization.openId.identity.MakeCodeGrantIdentityToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class MakeTokenFactory {
    private MakeCodeGrantIdentityToken makeCodeGrantIdentityToken;

    @Autowired
    public MakeTokenFactory(MakeCodeGrantIdentityToken makeCodeGrantIdentityToken) {
        this.makeCodeGrantIdentityToken = makeCodeGrantIdentityToken;
    }

    public MakeToken make(Extension extension) {
        MakeToken makeToken = null;
        if (extension == Extension.IDENTITY) {
            makeToken = new MakeOpenIdToken(makeCodeGrantIdentityToken);
        } else {
            makeToken = new MakeOAuthToken();
        }
        return makeToken;
    }
}
