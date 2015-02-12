Scenario: Required parameters

Given the parameter client ids has one item assigned to a randomly generated UUID
And the parameter response types has one item assigned to CODE
When a AuthRequest is created
Then the client id is equal to the input value
And the response type is CODE
And the redirect uri is a empty optional
And the scopes are null

Scenario: Required and Optional parameters

Given the parameter client ids has one item assigned to a randomly generated UUID
And the parameter response types has one item assigned to CODE
And the redirect uri is https://rootservices.org
And the scopes is a list with the first item’s value assigned to PROFILE
When a AuthRequest is created
Then the client id is equal to the input value
And the response type is CODE
And the redirect uri is https://rootservices.org
And the scopes is a list with the first item’s value assigned to scope