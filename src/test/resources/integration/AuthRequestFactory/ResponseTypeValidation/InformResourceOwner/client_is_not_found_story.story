Scenario: response type is null and client is not found

Given the parameter response types is assigned null
And the parameter client ids has one item assigned to a randomly generated UUID
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's code to be CLIENT_NOT_FOUND
And expect e's cause to be a RecordNotFoundException

Scenario: response type is a empty list and client is not found

Given the parameter response types has no items
And the parameter client ids has one item assigned to a randomly generated UUID
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's code to be CLIENT_NOT_FOUND
And expect e's cause to be a RecordNotFoundException

Scenario: response type has one item and it's invalid and client is not found

Given the parameter response types has one item and it's not CODE
And the parameter client ids has one item assigned to a randomly generated UUID
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's code to be CLIENT_NOT_FOUND
And expect e's cause to be a RecordNotFoundException

Scenario: response type has more than one item and client is not found

Given the parameter response types has two items assigned to CODE
And the parameter client ids has one item assigned to a randomly generated UUID
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's code to be CLIENT_NOT_FOUND
And expect e's cause to be a RecordNotFoundException