Feature: Verify images in IFrame page

  Scenario: Verify images change when clicking right arrow
    Given I launch "http://webdriveruniversity.com/index.html"
    Then the page title should contain "WebDriver University"
    When I click on "IFRAME"
    Then a new tab should open
    And I verify the image is displayed
    When I click the right arrow
    Then the image should change
