package helpers.fixture.persistence.http.input;

import net.tokensmith.repository.entity.ConfidentialClient;

import java.util.List;
import java.util.Map;

public class AuthEndpointProps {
    private ConfidentialClient confidentialClient;
    private String baseURI;
    private List<String> scopes;
    private String email;
    private Map<String, List<String>> params;

    public AuthEndpointProps(ConfidentialClient confidentialClient, String baseURI, List<String> scopes, String email, Map<String, List<String>> params) {
        this.confidentialClient = confidentialClient;
        this.baseURI = baseURI;
        this.scopes = scopes;
        this.email = email;
        this.params = params;
    }

    public ConfidentialClient getConfidentialClient() {
        return confidentialClient;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public String getEmail() {
        return email;
    }

    public Map<String, List<String>> getParams() {
        return params;
    }
}
