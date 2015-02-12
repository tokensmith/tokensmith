Scenario: response type is null and client is found

Given the parameter response types is assigned null
And a client exists in the database, c
And the parameter client ids has one item assigned to c's UUID
When a AuthRequest is created
Then expect a InformClientException to be thrown, e
And expect e's error to be invalid_request
And expect e's redirect uri to be equal to c's redirect uri
And expect e's cause to be a ResponseTypeException

Scenario: response type is a empty list and client is found

Given the parameter response types has no items
And a client exists in the database, c
And the parameter client ids has one item assigned to c's UUID
When a AuthRequest is created
Then expect a InformClientException to be thrown, e
And expect e's error to be invalid_request
And expect e's redirect uri to be equal to c's redirect uri
And expect e's cause to be a ResponseTypeException

Scenario: response type has one item and it's invalid and client is found

Given the parameter response types has one item and it's not CODE
And a client exists in the database, c
And the parameter client ids has one item assigned to c's UUID
When a AuthRequest is created
Then expect a InformClientException to be thrown, e
And expect e's error to be unsupported_response_type
And expect e's redirect uri to be equal to c's redirect uri
And expect e's cause to be a ResponseTypeException

Scenario: response type has more than one item and client is found

Given the parameter response types has two items assigned to CODE
And a client exists in the database, c
And the parameter client ids has one item assigned to c's UUID
When a AuthRequest is created
Then expect a InformClientException to be thrown, e
And expect e's error to be invalid_request
And expect e's redirect uri to be equal to c's redirect uri
And expect e's cause to be a ResponseTypeException
