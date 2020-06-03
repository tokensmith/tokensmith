# Login
A Java SDK to authorize and authenticate with [OIDC](https://openid.net/connect/) Identity Servers.

## Terminology
This SDK has chosen to use `resource owner` for the human or robot that the SDK is asking to authorize and authenticate.
OAuth2 uses, `resource owner`. OIDC uses, `end-user`.

## Configuration.
You'll need to set the following environment variables:
```bash
$ export CLIENT_ID=123456789
$ export CLIENT_USER_NAME=username
$ export CLIENT_PASSWORD=password
$ export LOGIN_URL=https://sso.tokensmith.net/api/public/v1/token
$ export TOKEN_URL=https://sso.tokensmith.net/api/public/v1/token
$ export USER_INFO_URL=https://sso.tokensmith.net/api/public/v1/userinfo
$ export AUTHORIZATION_URL=https://sso.tokensmith.net/authorization
$ export PUBLIC_KEY_URL=https://sso.tokensmith.net/api/public/v1/jwk/rsa/%s
$ export CORRELATION_ID_FIELD=correlation-id
```

you could also run:
```bash
$ ./vars.sh
```

or you can inject them into the `LoginFactory`
```java
LoginFactory loginFactory = new LoginFactory();

// add the secrets to the secrets map
Map<String, String> secrets = new HashMap<>();

// inject in secrets.
loginFactory.setSecrets(secrets);
```

## SDK
Have a look at `TokenSmithLogin` which is an implementation of `Login`. 
It has Javadocs which are hopefully enough to get started. 
There are also lots of tests, including integration tests with WireMock 
which are in `TokenSmithLoginTest`. 

### grant types
Supported grant types are:

##### password
 ```java
LoginFactory loginFactory = new LoginFactory();
Login login = loginFactory.tokenSmithLogin();

List<String> scopes = new ArrayList<>();
scopes.add("profile");
scopes.add("openid");

UserWithTokens userWithTokens = login.withPassword("user-name", "password", scopes);
 ```
 
##### refresh_token
```java
LoginFactory loginFactory = new LoginFactory();
Login login = loginFactory.tokenSmithLogin();

UserWithTokens userWithTokens = login.withRefreshToken("some-refresh-token");
```
##### code
```java
LoginFactory loginFactory = new LoginFactory();
Login login = loginFactory.tokenSmithLogin();

UserWithTokens userWithTokens = login.withCode("some-authorization-code", "some-nonce", redirectUri);
```

### user info
Get the claims about the authenticated resource owner.

```java
LoginFactory loginFactory = new LoginFactory();
Login login = loginFactory.tokenSmithLogin();

User user = login.userInfo("some-access-token");
```

### authorization endpoint
Generate the URI to the id server's authorization endpoint with the response type, code.

```java
LoginFactory loginFactory = new LoginFactory();
Login login = loginFactory.tokenSmithLogin();

String state = "state-123";
String redirectUri = "http://tokensmith.net/welcome";
List<String> scopes = new ArrayList<>();
scopes.add("profile");

Redirect authorizationEndpoint = login.authorizationEndpoint(state, redirectUri, scopes);
```




