package net.toknsmith.login.factory;

import net.toknsmith.login.config.props.EndpointProps;
import net.toknsmith.login.constant.ResponseType;
import net.toknsmith.login.model.Redirect;
import net.toknsmith.login.exception.URLException;
import net.toknsmith.login.http.Param;
import net.toknsmith.login.http.URLBuilder;
import net.toknsmith.login.security.RandomString;

import java.net.URL;
import java.util.List;


public class MakeRedirect {
    private EndpointProps endpointProps;
    private RandomString randomString;

    public MakeRedirect(EndpointProps endpointProps, RandomString randomString) {
        this.endpointProps = endpointProps;
        this.randomString = randomString;
    }

    public Redirect makeRedirect(String state, String redirectUri, List<String> scopes) throws URLException {
        String nonce = randomString.run();

        URL authorizationEndpoint = new URLBuilder()
                .baseUrl(endpointProps.getAuthorizationUrl())
                .param(Param.CLIENT_ID.toString(), endpointProps.getClientId())
                .param(Param.RESPONSE_TYPE.toString(), ResponseType.CODE.toString())
                .param(Param.REDIRECT_URI.toString(), redirectUri)
                .paramsWhiteSpaceDelimitted(Param.SCOPE.toString(), scopes)
                .param(Param.STATE.toString(), state)
                .param(Param.NONCE.toString(), nonce)
                .build();

        Redirect redirect = new Redirect(authorizationEndpoint, nonce, state);
        return redirect;
    }
}
