# Testing Q-Cumberless Testing
Feature: Hello World

    Scenario: I see "What to say?" when I launch the app
        Given my app is running
        Then I see "What to say?"
        And I don't see "Test Q-Cumberless!"

    Scenario: I see "Hello World!" when I press the button
        Given my app is running
        When I press the "Say it!" button
        Then I see "Hello World!"
        And I don't see "What to say?"

    Scenario: I see "Test Q-Cumberless!" when I select it in the spinner
        Given my app is running
        When I select "Test Q-Cumberless!" from "whatToSaySpinner"
        And I press the "Say it!" button
        Then I see "Test Q-Cumberless!"
        And I don't see "Hello World!"

    # A scratch? Your arm's off!
    @bug
    Scenario: I see "Tis' nothing but a scratch!"
        Given my app is running
        Then I see "Tis' nothing but a scratch!"


