package org.rootservices.authorization.http.factory;


import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.openId.identity.MakeCodeGrantIdentityToken;
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
