
## Token endpoint

### Create a token

 - [Authorization Code Grant](#authorization-code-grant)
 - [Password Grant](#password-grant)
 - [Refresh Token Grant](#refresh-token-grant)
 - [Token Endpoint Error Responses](#token-endpoint-error-responses)
    - [UnAuthorized](#unauthorized)
    - [Not Found](#not-found)
    - [Bad Request](#bad-request)

#### Authorization Code Grant

Required Fields:
 - grant_type
 - code

Optional Fields:
 - redirect_uri
 
```
POST /token HTTP/1.1
Host: sso.tokensmith.net
Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded
Accept: application/json;charset=UTF-8

grant_type=authorization_code&code=SplxlOBeZQQYbYS6WxSbIA
&redirect_uri=https%3a%2f%2ftokensmith.net
```

```
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Cache-Control: no-store
Pragma: no-cache

{
  "access_token":"2YotnFZFEjr1zCsicMWpAA",
  "token_type":"bearer",
  "expires_in":3600,
  "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA",
  "id_token": "secure-json-web-token"
}
```

#### Password Grant

Required Fields
 - grant_type
 - username
 - password
 
Optional Fields:
 - scope
 
```
POST /token HTTP/1.1
Host: sso.tokensmith.net
Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded
Accept: application/json;charset=UTF-8

grant_type=password&username=obi-wan@tokensmith.net&password=A3ddj3w&scope=openid%20profile
```

```
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Cache-Control: no-store
Pragma: no-cache

{
  "access_token":"2YotnFZFEjr1zCsicMWpAA",
  "token_type":"bearer",
  "expires_in":3600,
  "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA",
  "id_token": "secure-json-web-token"
}
```

#### Refresh Token Grant

Unlike the oauth2 spec client_id and client_secret are not required for this request.
Instead, the client is authenticated by using the Authorization header.

Required Fields:
 - grant_type
 - refresh_token
 
Optional Fields:
 - scope
 
```
POST /api/v1/token
Host: sso.tokensmith.net
Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded
Accept: application/json;charset=UTF-8

grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
&scope=openid%20profile
```

```
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Cache-Control: no-store
Pragma: no-cache

{
  "access_token":"2YotnFZFEjr1zCsicMWpAA",
  "token_type":"bearer",
  "expires_in":3600,
  "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA",
  "id_token": "secure-json-web-token"
}
```

#### Token Endpoint Error Responses

##### UnAuthorized

Authorization Header missing

```
POST /api/v1/token
Host: sso.tokensmith.net
Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded
Accept: application/json;charset=UTF-8

grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
&scope=openid%20profile
```

```
HTTP/1.1 401 UNAUTHORIZED
Content-Type: application/json;charset=UTF-8
WWW-Authenticate: Basic
Cache-Control: no-store
Pragma: no-cache

{
  "error": "invalid_client"
}
```

Authorization Scheme is not Basic

```
POST /api/v1/token
Host: sso.tokensmith.net
Authorization: Bearer czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded
Accept: application/json;charset=UTF-8

grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
&scope=openid%20profile
```

```
HTTP/1.1 401 UNAUTHORIZED
Content-Type: application/json;charset=UTF-8
WWW-Authenticate: Basic
Cache-Control: no-store
Pragma: no-cache

{
  "error": "invalid_client"
}
```

Client failed authentication

```
POST /api/v1/token
Host: sso.tokensmith.net
Authorization: Basic wrong-client-credentials
Content-Type: application/x-www-form-urlencoded
Accept: application/json;charset=UTF-8

grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
&scope=openid%20profile
```

```
HTTP/1.1 401 UNAUTHORIZED
Content-Type: application/json;charset=UTF-8
WWW-Authenticate: Basic
Cache-Control: no-store
Pragma: no-cache

{
  "error": "invalid_client"
}
```

##### Not Found

The following reasons will cause this error response:
 - Authorization Code not found
 - Redirect URI mismatch
 - Refresh Token not found
 - Resource Owner not found

```
HTTP/1.1 404 NOT FOUND
Content-Type: application/json;charset=UTF-8
WWW-Authenticate: Basic
Cache-Control: no-store
Pragma: no-cache

{
  "error": "invalid_grant"
}
```

##### Bad Request 

Duplicate Field in message body

```
POST /api/v1/token
Host: sso.tokensmith.net
Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded
Accept: application/json;charset=UTF-8

grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
&client_id=s6BhdRkqt3&client_secret=7Fjfp0ZBr1KtDRbnfVdmIw&scope=openid%20profile
&client_id=s6BhdRkqt3
```

```
HTTP/1.1 400 BAD REQUEST
Content-Type: application/json;charset=UTF-8
WWW-Authenticate: Basic
Cache-Control: no-store
Pragma: no-cache

{
  "error": "invalid_request",
  "description": "client_id  is repeated"
}
```


Missing Field in message body or a field's value is empty

```
POST /api/v1/token
Host: sso.tokensmith.net
Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded
Accept: application/json;charset=UTF-8

grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
&client_secret=7Fjfp0ZBr1KtDRbnfVdmIw&scope=openid%20profile
```

```
HTTP/1.1 400 BAD REQUEST
Content-Type: application/json;charset=UTF-8
WWW-Authenticate: Basic
Cache-Control: no-store
Pragma: no-cache

{
  "error": "invalid_request",
  "description": "client_id is a required field"
}
```

Unknown Field in message body

```
POST /api/v1/token
Host: sso.tokensmith.net
Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded
Accept: application/json;charset=UTF-8

grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
&client_id=s6BhdRkqt3&client_secret=7Fjfp0ZBr1KtDRbnfVdmIw&scope=openid%20profile
&unknown_field=foo
```

```
HTTP/1.1 400 BAD REQUEST
Content-Type: application/json;charset=UTF-8
WWW-Authenticate: Basic
Cache-Control: no-store
Pragma: no-cache

{
  "error": "invalid_request",
  "description": "unknown_field is a unknown key"
}
```

Invalid Payload - most likely message is not the content-type, application/x-www-form-urlencoded

```
POST /api/v1/token
Host: sso.tokensmith.net
Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded
Accept: application/json;charset=UTF-8

{XXXX} some malformed message body }}
```

```
HTTP/1.1 400 BAD REQUEST
Content-Type: application/json;charset=UTF-8
WWW-Authenticate: Basic
Cache-Control: no-store
Pragma: no-cache

{
  "error": "invalid_request",
  "description": "payload is not json"
}
```

Invalid Value - scope has no value

```
POST /api/v1/token
Host: sso.tokensmith.net
Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded
Accept: application/json;charset=UTF-8

grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
&scope=
```

```
HTTP/1.1 400 BAD REQUEST
Content-Type: application/json;charset=UTF-8
WWW-Authenticate: Basic
Cache-Control: no-store
Pragma: no-cache

{
  "error": "invalid_request",
  "description": "scope is invalid"
}
```

Authorization Code is compromised (has already been used)

```
POST /token HTTP/1.1
Host: sso.tokensmith.net
Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded
Accept: application/json;charset=UTF-8

grant_type=authorization_code&code=SplxlOBeZQQYbYS6WxSbIA
&redirect_uri=https%3a%2f%2ftokensmith.net
```

```
HTTP/1.1 400 BAD REQUEST
Content-Type: application/json;charset=UTF-8
WWW-Authenticate: Basic
Cache-Control: no-store
Pragma: no-cache

{
  "error": "invalid_grant",
  "description": "the authorization code was already used"
}
```

Refresh Token is compromised (has already been used)

```
POST /api/v1/token
Host: sso.tokensmith.net
Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded
Accept: application/json;charset=UTF-8

grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
&scope=openid%20profile
```

```
HTTP/1.1 400 BAD REQUEST
Content-Type: application/json;charset=UTF-8
WWW-Authenticate: Basic
Cache-Control: no-store
Pragma: no-cache

{
  "error": "invalid_grant",
  "description": "the refresh token was already used"
}
```


Requested scope is invalid
 - Occurs when a requested scope was not included in original token.
 - The client does not have the requested scope associated with it.

```
POST /api/v1/token
Host: sso.tokensmith.net
Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded
Accept: application/json;charset=UTF-8

grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
&scope=openid%20profile
```

```
HTTP/1.1 400 BAD REQUEST
Content-Type: application/json;charset=UTF-8
WWW-Authenticate: Basic
Cache-Control: no-store
Pragma: no-cache

{
  "error": "invalid_scope",
  "description": "scope is not available for this client"
}
```
