package net.tokensmith.authorization.http.controller.resource.html.authorization.helper;

import helpers.category.UnitTests;
import net.tokensmith.authorization.http.controller.resource.html.authorization.oauth.OAuth2CodeResource;
import net.tokensmith.authorization.http.controller.resource.html.authorization.oauth.OAuth2ImplicitResource;
import net.tokensmith.authorization.http.controller.resource.html.authorization.openid.OpenIdCodeResource;
import net.tokensmith.authorization.http.controller.resource.html.authorization.openid.OpenIdImplicitIdentityResource;
import net.tokensmith.authorization.http.controller.resource.html.authorization.openid.OpenIdImplicitResource;
import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import net.tokensmith.otter.controller.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@Category(UnitTests.class)
public class AuthorizationFactoryTest {
    private static String OPENID_SCOPE = "openid";
    private static String IMPLICIT_GRANT = "token";
    private static String AUTHORIZATION_CODE_GRANT = "code";
    private static String IDENTITY_RESPONSE_TYPE = "id_token";

    private AuthorizationFactory subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new AuthorizationFactory(
                new OAuth2CodeResource(),
                new OAuth2ImplicitResource(),
                new OpenIdCodeResource(),
                new OpenIdImplicitResource(),
                new OpenIdImplicitIdentityResource()
        );
    }

    @Test
    public void isOpenIdWhenOpenIdShouldReturnTrue() throws Exception {
        List<String> scopes = new ArrayList<>();
        scopes.add(OPENID_SCOPE);

        boolean actual = subject.isOpenId(scopes);

        assertThat(actual, is(true));
    }

    @Test
    public void isOpenIdWhenProfileAndOpenIdShouldReturnTrue() throws Exception {
        List<String> scopes = new ArrayList<>();
        scopes.add("profile");
        scopes.add(OPENID_SCOPE);

        boolean actual = subject.isOpenId(scopes);

        assertThat(actual, is(true));
    }

    @Test
    public void isOpenIdShouldReturnFalse() throws Exception {
        List<String> scopes = new ArrayList<>();
        scopes.add("foo");

        boolean actual = subject.isOpenId(scopes);

        assertThat(actual, is(false));
    }

    @Test
    public void makeResourceWhenTokenAndIdTokenShouldBeOpenIdImplicitResource() {
        List<String> scopes = new ArrayList<>();
        scopes.add(OPENID_SCOPE);

        List<String> responseTypes = new ArrayList<>();
        responseTypes.add(IMPLICIT_GRANT + " " + IDENTITY_RESPONSE_TYPE);

        Resource<WebSiteSession, WebSiteUser> actual = subject.makeResource(scopes, responseTypes);

        assertThat(actual, instanceOf(OpenIdImplicitResource.class));
    }

    @Test
    public void makeResourceWhenIdTokenShouldBeOpenIdImplicitIdentityResource() {
        List<String> scopes = new ArrayList<>();
        scopes.add(OPENID_SCOPE);

        List<String> responseTypes = new ArrayList<>();
        responseTypes.add(IDENTITY_RESPONSE_TYPE);

        Resource<WebSiteSession, WebSiteUser> actual = subject.makeResource(scopes, responseTypes);

        assertThat(actual, instanceOf(OpenIdImplicitIdentityResource.class));
    }

    @Test
    public void makeResourceWhenResponseTypesIsNullAndOpenIdShouldBeOpenIdCodeResource() {
        List<String> scopes = new ArrayList<>();
        scopes.add(OPENID_SCOPE);

        List<String> responseTypes = null;

        Resource<WebSiteSession, WebSiteUser> actual = subject.makeResource(scopes, responseTypes);

        assertThat(actual, instanceOf(OpenIdCodeResource.class));
    }

    @Test
    public void makeResourceShouldBeOpenIdCodeResource() {
        List<String> scopes = new ArrayList<>();
        scopes.add(OPENID_SCOPE);

        List<String> responseTypes = new ArrayList<>();
        responseTypes.add(AUTHORIZATION_CODE_GRANT);

        Resource<WebSiteSession, WebSiteUser> actual = subject.makeResource(scopes, responseTypes);

        assertThat(actual, instanceOf(OpenIdCodeResource.class));
    }

    @Test
    public void makeResourceShouldBeOAuth2ImplicitResource() {
        List<String> scopes = new ArrayList<>();

        List<String> responseTypes = new ArrayList<>();
        responseTypes.add(IMPLICIT_GRANT);

        Resource<WebSiteSession, WebSiteUser> actual = subject.makeResource(scopes, responseTypes);

        assertThat(actual, instanceOf(OAuth2ImplicitResource.class));
    }

    @Test
    public void makeResourceShouldBeOAuth2CodeResource() {
        List<String> scopes = new ArrayList<>();

        List<String> responseTypes = new ArrayList<>();
        responseTypes.add(AUTHORIZATION_CODE_GRANT);

        Resource<WebSiteSession, WebSiteUser> actual = subject.makeResource(scopes, responseTypes);

        assertThat(actual, instanceOf(OAuth2CodeResource.class));
    }

    @Test
    public void makeResourceShouldBeDefault() {
        List<String> scopes = new ArrayList<>();

        List<String> responseTypes = new ArrayList<>();
        responseTypes.add("foo");

        Resource<WebSiteSession, WebSiteUser> actual = subject.makeResource(scopes, responseTypes);

        assertThat(actual, instanceOf(OAuth2CodeResource.class));
    }
}