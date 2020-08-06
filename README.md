# Tokensmith
Tokensmith is a Java implementation of an [OAuth 2.0](http://tools.ietf.org/html/rfc6749) and [OIDC](https://openid.net/) Identity server.

### To start using Tokensmith

Have a look at the documentation on [tokensmith.net](https://tokensmith.net)

### To write features

[Contributing docs](https://tokensmith.net/docs/contribute/) are available which hopefully will be sufficient to get started.
If you find they are not, then submit an issue in the [website repo](https://github.com/tokensmith/website).

## Repo layout
This repo has multiple gradle projects.

### [http](http)
Everything related to accepting and responding to HTTP requests
### [core](core)
Use cases for supporting OAuth2 and OIDC.
### [repository](repository)
Entities and Repository interfaces
### [login](login)
An SDK to interact with Tokensmith (OIDC ID Server).
