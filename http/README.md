HTTP for Authorization
-----------------------

## Interactions
 - [register](localhost:8080/register) a resource user
 - [reset a password](localhost:8080/forgot-password) for a resource owner
 - [authorization endpoint](http://localhost:8080/authorization?client_id=48d4f828-69bc-4e34-81e3-28288fa4de7a&response_type=CODE&scopes=openid+profile&redirect_uri=https://tokensmith.net) with code response type.
 - [authorization endpoint](http://localhost:8080/authorization?client_id=3ea070d8-c687-4ebc-be2f-32dfb1acd372&response_type=TOKEN&scopes=openid+profile&redirect_uri=https://tokensmith.net) with token response type.
 
## API
 - [user-info](docs/user-info.md)
 - [token](docs/token.md)
 - keys



