## User info Endpoint

 - [Find out if a user is logged in](#find-out-if-a-user-is-logged-in)
 - [User Info Error Responses](user-info-error-responses)
    - [UnAuthorized](#unauthorized)
    - [Server Error](#server-error)
 - [Create a user}(#create-a-user)

### Find out if a user is logged in
```
GET /api/v1/userinfo
Content-Type: application/jwt;charset=UTF-8
Accept: application/jwt;charset=UTF-8
Authorization: Bearer some-access-token
```

```
HTTP/1.1 200 OK
Content-Type: application/jwt;charset=UTF-8
Cache-Control: no-store
Pragma: no-cache

eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IjA0YjJiMDg0LTBhOTMtNDcxMC1hMzE2LWMxMGMyMmQ1OTYwOCJ9.eyJpc3MiOiJodHRwczovL3Nzby5yb290c2VydmljZXMub3JnIiwiYXVkIjpbIjQ0MDg1Mjk2LWY1ZDEtNDk4MS05OTAxLWNiYjQwMThhNTgxMSJdLCJleHAiOjE0OTQxNjg1NjIsImlhdCI6MTQ5NDE2NDk2MiwiZW1haWwiOiJ0ZXN0LWYxZDE0NjJmLTcwMjQtNGUxMC04Zjg3LTAzNzcwMjdiNTE4MkByb290c2VydmljZXMub3JnIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJhdXRoX3RpbWUiOjE0OTQxNjQ5NjJ9.qFLBIqCa0_e0FBCsbjPRdBjNL3m_mLDJksY5izgFwubnNUlf-RhK6qIGct8gCxCuOB2KDHZ1sfixWjjrVARq6Dnu37Mzo_0KLBcvpw2nuaA7_GnZnhU-vl-p3KZ1bo_hUKoNqWLIE0cOgBxFJr6W0WhT9T8vLJTIAqb7dkhGmdhZQ1vHFinMDWD-aXTFYm-MkAE_GFamOCIYmm-2RBNRSW8yaUKGMbjgH1rpBc7mfqE07JjUhucNNZgBTTouAvvfupP7yKhJxypBMpRWUG-mfx8S9Gp5vr1d2zYdjdejSKV3Fpy2pnoovUYLt84WyALhafzucuVLKadrDt8uAQxmBQ
```

### User Info Error Responses

#### UnAuthorized

Authorization Header missing

```
GET /api/v1/userinfo
Host: sso.rootservices.org
Content-Type: application/jwt;charset=UTF-8
Accept: application/jwt;charset=UTF-8
```

```
HTTP/1.1 401 UNAUTHORIZED
Content-Type: application/json;charset=UTF-8
WWW-Authenticate: Bearer
Cache-Control: no-store
Pragma: no-cache
```

Authorization Scheme is not Bearer

```
GET /api/v1/userinfo
Host: sso.rootservices.org
Authorization: Basic some-basic-credentials
Content-Type: application/jwt;charset=UTF-8
Accept: application/jwt;charset=UTF-8
```

```
HTTP/1.1 401 UNAUTHORIZED
Content-Type: application/json;charset=UTF-8
WWW-Authenticate: Bearer
Cache-Control: no-store
Pragma: no-cache
```

Failed Authentication

```
GET /api/v1/userinfo
Host: sso.rootservices.org
Authorization: Bearer access-token
Content-Type: application/jwt;charset=UTF-8
Accept: application/jwt;charset=UTF-8
```

```
HTTP/1.1 401 UNAUTHORIZED
Content-Type: application/json;charset=UTF-8
WWW-Authenticate: error="invalid_token"
Cache-Control: no-store
Pragma: no-cache
```

#### Server Error

Key to sign JWT was not found

```
GET /api/v1/userinfo
Host: sso.rootservices.org
Authorization: Bearer access-token
Content-Type: application/jwt;charset=UTF-8
Accept: application/jwt;charset=UTF-8
```

```
HTTP/1.1 500 SERVER ERROR
Content-Type: application/json;charset=UTF-8
Cache-Control: no-store
Pragma: no-cache
```

Failed to use key to sign JWT
 - invalid algorithm
 - wrong key type
 - could not marshal jwt to json

```
GET /api/v1/userinfo
Host: sso.rootservices.org
Authorization: Bearer access-token
Content-Type: application/jwt;charset=UTF-8
Accept: application/jwt;charset=UTF-8
```

```
HTTP/1.1 500 SERVER ERROR
Content-Type: application/json;charset=UTF-8
Cache-Control: no-store
Pragma: no-cache
```

### Create a user

Required Fields:
 - email
 - password

```
POST /api/v1/userinfo
Content-Type: application/json;charset=UTF-8
Accept: application/json;charset=UTF-8
{
  "email": "obi-wan@rootservices.org",
  "password": "password",
  "name": "Obi-Wan Kenobi",
  "family_name": "Kenobi",
  "given_name": "Obi-Wan",
  "middle_name": "Wan",
  "nick_name": "Ben",
  "preferred_user_name": "Ben Kenobi",
  "profile": "http://starwars.wikia.com/wiki/Obi-Wan_Kenobi",
  "picture": "http://vignette1.wikia.nocookie.net/starwars/images/2/25/Kenobi_Maul_clash.png/revision/latest?cb=20130120033039",
  "website": "http://starwars.wikia.com",
  "gender": "male",
  "birthdate": "3220-01-01",
  "zoneinfo": "America/Chicago",
  "locale": "en-US",
  "phone_number": "123-456-7891",
  "address" : {
    "street_address1": "123 Best Jedi Lane",
    "street_address2": "#1",
    "locality": "Chicago",
    "region": "IL",
    "postal_code": "60606",
    "country": "Coruscant"
  }
}
```

```
HTTP/1.1 201 OK
Content-Type: application/json;charset=UTF-8
Cache-Control: no-store
Pragma: no-cache
location: /api/v1/userinfo
```
