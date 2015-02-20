Scenario: response type is null, client is found, redirect uri mismatch

Given the parameter response types is assigned null
And a client, c
And c's redirect uri is https://rootservices.org/continue
And c is persisted to the database
And the parameter client ids has one item assigned to c's UUID
And the parameter redirect uris has one item assigned to https://rootservices.org
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's code to be REDIRECT_URI_MISMATCH
And expect e's cause to be a ResponseTypeException

Scenario: response type is a empty list, client is found, redirect uri mismatch

Given the parameter response types has no items
And a client, c
And c's redirect uri is https://rootservices.org/continue
And c is persisted to the database
And the parameter client ids has one item assigned to c's UUID
And the parameter redirect uris has one item assigned to https://rootservices.org
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's code to be REDIRECT_URI_MISMATCH
And expect e's cause to be a ResponseTypeException

Scenario: response type has one item and it's invalid, client is found, redirect uri mismatch

Given the parameter response types has one item and it's not CODE
And a client, c
And c's redirect uri is https://rootservices.org/continue
And c is persisted to the database
And the parameter client ids has one item assigned to c's UUID
And the parameter redirect uris has one item assigned to https://rootservices.org
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's code to be REDIRECT_URI_MISMATCH
And expect e's cause to be a ResponseTypeException

Scenario: response type has more than one item, client is found, redirect uri mismatch

Given the parameter response types has two items assigned to CODE
And a client, c
And c's redirect uri is https://rootservices.org/continue
And c is persisted to the database
And the parameter client ids has one item assigned to c's UUID
And the parameter redirect uris has one item assigned to https://rootservices.org
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's code to be REDIRECT_URI_MISMATCH
And expect e's cause to be a ResponseTypeException
