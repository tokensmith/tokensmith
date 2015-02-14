Scenario: redirect uris has more than one item

Given the parameter redirect uris has two items assigned to https://rootservices.org
And the parameter client ids has one item assigned to a randomly generated UUID
And the parameter response types has one item assigned to CODE
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's cause to be a RedirectUriException

Scenario: redirect uris has one item and it's not https

Given the parameter redirect uris has one item assigned to http://rootservices.org
And the parameter client ids has one item assigned to a randomly generated UUID
And the parameter response types has one item assigned to CODE
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's cause to be a RedirectUriException

Scenario: redirect uris has one item that is not a URI

Given the parameter redirect uris has one item assigned to INVALID_URI
And the parameter client ids has one item assigned to a randomly generated UUID
And the parameter response types has one item assigned to CODE
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's cause to be a RedirectUriException