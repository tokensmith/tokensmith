package helpers.fixture.persistence.http.input;

import net.tokensmith.repository.entity.ConfidentialClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthEndpointPropsBuilder {
    private ConfidentialClient confidentialClient;
    private String baseURI;
    private List<String> scopes;
    private Map<String, List<String>> extraQueryParams = new HashMap<>();
    private String email;


    public AuthEndpointPropsBuilder confidentialClient(ConfidentialClient confidentialClient) {
        this.confidentialClient = confidentialClient;
        return this;
    }

    public AuthEndpointPropsBuilder baseURI(String baseURI) {
        this.baseURI = baseURI;
        return this;
    }

    public AuthEndpointPropsBuilder scopes(List<String> scopes) {
        this.scopes = scopes;
        return this;
    }

    public AuthEndpointPropsBuilder extraQueryParams(Map<String, List<String>> extraQueryParams) {
        this.extraQueryParams = extraQueryParams;
        return this;
    }

    public AuthEndpointPropsBuilder addQueryParam(String key, String value) {
        List<String> values = extraQueryParams.get(key);
        if (values == null || values.size() == 0) {
            extraQueryParams.put(key, Collections.singletonList(value));
        } else {
            values.add(value);
            extraQueryParams.put(key, values);
        }
        return this;
    }

    public AuthEndpointPropsBuilder email(String email) {
        this.email = email;
        return this;
    }

    public AuthEndpointProps build() {
        return new AuthEndpointProps(confidentialClient, baseURI, scopes, email, extraQueryParams);
    }
}
