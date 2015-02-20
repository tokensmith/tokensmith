Scenario: scopes has more than one item and client is not found

Given the parameter scopes has two items assigned to PROFILE
And the parameter client ids has one item assigned to a randomly generated UUID
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's code to be CLIENT_NOT_FOUND
And expect e's cause to be a RecordNotFoundException

Scenario: scopes has one item and it's invalid and client is not found

Given the parameter scopes has one item assigned to UNKNOWN_SCOPE
And the parameter client ids has one item assigned to a randomly generated UUID
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's code to be CLIENT_NOT_FOUND
And expect e's cause to be a RecordNotFoundException

Scenario: scopes has one item and it's a empty string and client is not found

Given the parameter scopes has one item assigned to a empty string
And the parameter client ids has one item assigned to a randomly generated UUID
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's code to be CLIENT_NOT_FOUND
And expect e's cause to be a RecordNotFoundException
