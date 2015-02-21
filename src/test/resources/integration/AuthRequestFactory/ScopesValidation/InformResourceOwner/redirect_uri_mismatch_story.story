Scenario: scopes has more than one item, client is found, redirect uri mismatch

Given the parameter scopes has two items assigned to PROFILE
And a client, c
And c's redirect uri is https://rootservices.org/continue
And c is persisted to the database
And the parameter client ids has one item assigned to c's UUID
And the parameter redirect uris has one item assigned to https://rootservices.org
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's code to be REDIRECT_URI_MISMATCH
And expect e's cause to be a ResponseTypeException

Scenario: scopes has one item and it's invalid, client is found, redirect uri mismatch

Given the parameter scopes has one item assigned to UNKNOWN_SCOPE
And a client, c
And c's redirect uri is https://rootservices.org/continue
And c is persisted to the database
And the parameter client ids has one item assigned to c's UUID
And the parameter redirect uris has one item assigned to https://rootservices.org
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's code to be REDIRECT_URI_MISMATCH
And expect e's cause to be a ResponseTypeException

Scenario: scopes has one item and it's a empty string, client is found, redirect uri mismatch

Given the parameter scopes has one item assigned to a empty string
And a client, c
And c's redirect uri is https://rootservices.org/continue
And c is persisted to the database
And the parameter client ids has one item assigned to c's UUID
And the parameter redirect uris has one item assigned to https://rootservices.org
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's code to be REDIRECT_URI_MISMATCH
And expect e's cause to be a ResponseTypeException
