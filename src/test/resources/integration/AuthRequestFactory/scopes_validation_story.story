Scenario: scopes has more than one item

Given the parameter scopes has two items assigned to PROFILE
And a client exists in the database, c
And the parameter client ids has one item assigned to c's UUID
And the parameter response types has one item assigned to CODE
When a AuthRequest is created
Then expect a InformClientException to be thrown, e
And expect e's error to be invalid_request
And expect e's redirect uri to be equal to c's redirect uri
And expect e's cause to be a ScopesException

Scenario: scopes has one item and it's invalid

Given the parameter scopes has one item assigned to UNKNOWN_SCOPE
And a client exists in the database, c
And the parameter client ids has one item assigned to c's UUID
And the parameter response types has one item assigned to CODE
When a AuthRequest is created
Then expect a InformClientException to be thrown, e
And expect e's error to be invalid_scope
And expect e's redirect uri to be equal to c's redirect uri
And expect e's cause to be a ScopesException
