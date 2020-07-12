# Front End 

## Template usage
| Location  | Template | Link |
| -------------------- | ------------- |-------|
| ProfileResource | profile.jsp | login first, follow this [link](http://localhost:8080/profile) |
| ProfileResource | 401.jsp | with out logging in, follow this [link](http://localhost:8080/profile) |
| OpenIdImplicitResource | authorization.jsp | need to seed data for this to work. |
| OpenIdImplicitIdentityResource | authorization.jsp | need to seed data for this to work. |
| OpenIdCodeResource | authorization.jsp | follow this [link](http://localhost:8080/authorization?client_id=48d4f828-69bc-4e34-81e3-28288fa4de7a&response_type=CODE&scope=openid+profile&redirect_uri=https://tokensmith.net)|
| OAuth2CodeResource | authorization.jsp | need to seed data for this to work. |
| OAuth2ImplicitResource | authorization.jsp | need to seed data for this to work. |
| RegisterResource | register.jsp | follow this [link](http://localhost:8080/register) |
| RegisterResource | register-ok.jsp | finish registering with this [link](http://localhost:8080/register) and no `redirect` cookie. |
| WelcomeResource | welcome.jsp | follow link in email after registering |
| WelcomeResource | welcome-error.jsp | follow this [link](http://localhost:8080/welcome?nonce=not-a-nonce) |
| ForgotPasswordResource | forgot-password.jsp | follow this [link](http://localhost:8080/forgot-password) |
| ForgotPasswordResource | forgot-password-ok.jsp | post a new password to the forgot password [link](http://localhost:8080/forgot-password) |
| UpdatePasswordResource | update-password.jsp | follow link sent in email after forgot password successful |
| UpdatePasswordResource | update-password-expired.jsp | follow link sent in email after link expires |
| UpdatePasswordResource | update-password-ok.jsp | follow link sent in email after forgot password successful then post a successful message |
| UpdatePasswordResource | update-password-error.jsp | follow this [link](http://localhost:8080/update-password?nonce=not-a-nonce) |
| TokenSmithConfig | 401.jsp | |
| TokenSmithConfig | 403.jsp | |
| AuthorizationHelper | 404.jsp | follow this [link](http://localhost:8080/not-found) |
| MediaTypeResource | 415.jsp | |
| AuthorizationHelper | 500.jsp | |
