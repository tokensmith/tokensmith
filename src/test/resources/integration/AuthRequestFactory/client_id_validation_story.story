Scenario: client ids is null

Given the parameter client ids is assigned null
And the parameter response types has one item assigned to CODE
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's code to be CLIENT_ID_NULL
And expect e's cause to be a ClientIdException

Scenario: client ids is a empty list

Given the parameter client ids has no items
And the parameter response types has one item assigned to CODE
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's code to be CLIENT_ID_EMPTY_LIST
And expect e's cause to be a ClientIdException

Scenario: client ids has one item and it's invalid

Given the parameter client ids has one item and it's not a UUID
And the parameter response types has one item assigned to CODE
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's code to be CLIENT_ID_DATA_TYPE
And expect e's cause to be a ClientIdException

Scenario: client ids has more than one item

Given the parameter client ids has two randomly generated UUIDs
And the parameter response types has one item assigned to CODE
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown, e
And expect e's code to be CLIENT_ID_MORE_THAN_ONE_ITEM
And expect e's cause to be a ClientIdException
