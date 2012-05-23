Feature: Hello World

    Scenario: I see "What to say?" when I launch the app
        Given My app is running
        Then I see "What to say?"
        And I don't see "Test Q-Cumberless!"

    Scenario: I see "Hello World!" when I press the button
        Given My app is running
        When I press the "Say it!" button
        Then I see "Hello World!"
        And I don't see "What to say?"

    Scenario: I see "Test Q-Cumberless!" when I select it in the spinner
        Given My app is running
        When I select "Test Q-Cumberless!" from "whatToSaySpinner"
        And I press the "Say it!" button
        Then I see "Test Q-Cumberless!"
        And I don't see "Hello World!"


