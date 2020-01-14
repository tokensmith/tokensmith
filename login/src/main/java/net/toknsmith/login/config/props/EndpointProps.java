package net.toknsmith.login.config.props;

import java.net.URI;

public class EndpointProps {
    private String clientCredentials;
    private URI tokenEndpoint;
    private URI userInfoEndpoint;
    private String clientId;
    private URI authorizationUrl;

    public String getClientCredentials() {
        return clientCredentials;
    }

    public void setClientCredentials(String clientCredentials) {
        this.clientCredentials = clientCredentials;
    }

    public URI getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(URI tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public URI getUserInfoEndpoint() {
        return userInfoEndpoint;
    }

    public void setUserInfoEndpoint(URI userInfoEndpoint) {
        this.userInfoEndpoint = userInfoEndpoint;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public URI getAuthorizationUrl() {
        return authorizationUrl;
    }

    public void setAuthorizationUrl(URI authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }
}
