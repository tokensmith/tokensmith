Feature: Authorization Request for grant type code raises no exceptions.

  Scenario: Create with required parameters

    Given a randomly generated client_id
    And the response type is code
    When a AuthRequest is created
    Then the client id is equal to the input value
    And the response type is CODE
    And the redirect uri is null
    And the scopes are null

  Scenario: Create with required and optional parameters

    Given a randomly generated client_id
    And the response type is code
    And the redirect uri is https://rootservices.org
    And the scopes is a list with the first item’s value assigned to scope
    When a AuthRequest is created
    Then the client id is equal to the input value
    And the response type is CODE
    And the redirect uri is https://rootservices.org
    And the scopes is a list with the first item’s value assigned to scope