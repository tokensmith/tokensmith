Scenario: client ids is null

Given the parameter client ids is assigned null
And the parameter response types has one item assigned to CODE
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown
And expect the cause to be a ClientIdException

Scenario: client ids is a empty list

Given the parameter client ids has no items
And the parameter response types has one item assigned to CODE
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown
And expect the cause to be a ClientIdException

Scenario: client ids has one item and it's invalid

Given the parameter client ids has one item and it's not a UUID
And the parameter response types has one item assigned to CODE
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown
And expect the cause to be a ClientIdException

Scenario: client ids has more than one item

Given the parameter client ids has two randomly generated UUIDs
And the parameter response types has one item assigned to CODE
When a AuthRequest is created
Then expect a InformResourceOwnerException to be thrown
And expect the cause to be a ClientIdException
