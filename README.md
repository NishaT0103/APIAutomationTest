**Message API Test**
This project contains a set of automated tests for the Message API using RestAssured and JUnit 5. The tests cover creating, retrieving, updating, and deleting messages, as well as handling various edge cases.

**Prerequisites**
Java 8 or higher
Maven
An IDE such as Eclipse or IntelliJ IDEA
**Setup Instructions**
1. Clone the Repository
  **sh**
  git clone https://github.com/yourusername/MessageAPITest.git
  cd MessageAPITest

2. Open the Project in Your IDE
  Import the project as a Maven project in your preferred IDE.

3.Configure the Base URL
Ensure the BASE_URL in the messageAPITest.java file is set to the correct URL of your API.

**Running the Tests**
1. Using Maven
  Open a terminal and navigate to the project directory and Run the following command to execute the tests:
  **sh**
  mvn test
2. Using an IDE
  Open the project in your IDE.
  Navigate to the messageAPITest.java file.
  Run the tests using the built-in JUnit test runner.

**Test Overview**
 The test suite includes the following tests:
1. testCreateMessage: Tests creating a new message.
2. testGetMessagesBetweenUsers: Tests retrieving messages between two users.
3. testGetMessageById: Tests retrieving a message by its ID.
4. testGetMessagesBetweenUsers_NoMessages: Tests retrieving messages between two users where no messages exist.
5. testGetMessagesBetweenUsers_NoMessages01: Another test for retrieving messages between two users with no messages.
6. testGetMessageById_NonExistent: Tests retrieving a non-existent message by ID.
7. testCreateMessage_MissingField: Tests creating a message with a missing field.
8. testUpdateMessageById: Tests updating a message by its ID.
9. testUpdateMessageById_NonExistent: Tests updating a non-existent message by ID.
10. testDeleteMessageById: Tests deleting a message by its ID.
11. testDeleteMessageById_NonExistent: Tests deleting a non-existent message by ID.

**Test Details**
**Test Methods**
1. testCreateMessage: Creates a new message and verifies the response.
2. testGetMessagesBetweenUsers: Retrieves messages between two users and verifies the response.
3. testGetMessageById: Retrieves a message by its ID and verifies the response.
4. testGetMessagesBetweenUsers_NoMessages: Checks the response when no messages exist between the specified users.
5. testGetMessagesBetweenUsers_NoMessages01: Another check for when no messages exist between specified users.
6. testGetMessageById_NonExistent: Attempts to retrieve a non-existent message and checks for a 404 status code.
7. testCreateMessage_MissingField: Attempts to create a message with a missing field and expects a 400 status code.
8. testUpdateMessageById: Updates an existing message and verifies the response.
9. testUpdateMessageById_NonExistent: Attempts to update a non-existent message and expects a 404 status code.
10. testDeleteMessageById: Deletes an existing message and verifies the response.
11. testDeleteMessageById_NonExistent: Attempts to delete a non-existent message and expects a 404 status code.

**Assertions**
The tests use various assertions to verify the expected outcomes, such as:
1. Status codes
2. Response body fields
3. Error messages

**Debugging**
The tests include print statements to help with debugging in case of failures. These statements output the response body and status codes for failed requests.

**Defects Identified**
1. Message gets displayed between the users even if there is no message
   Test API used: GET /message?from=fromUserId&to=toUserId
   Pre-Condition: No message should be created between two users
   **** Steps to reproduce:****
   1. Enter the fromUserID of existing user in payload
   2. Enter the invalid toUserId of existing user in payload
   3. Get the response
   Expected result: Received response should contain the status code 404 (bad response code)
   Actual result: Received response contains the status code 200 (Success code)

2. When executing the testUpdateMessageById method in the messageAPITest class, a connection error occurs. This issue is observed because the server code is configured to handle user-related updates, not message-related updates, leading to a mismatch between the test expectations and the server functionality.

**Steps to Reproduce**
Observe the Error: A connection error is thrown.
Expected Behavior: The test should successfully update a message by its ID and return a 200 OK status code with the updated message details in the response body.
Actual Behavior: A connection error occurs because the server is not configured to handle message updates at the specified endpoint.

**Server Code Mismatch**
The server code provided is for updating user information, not messages. Here is the relevant server code snippet:
app.put('/api/messages/:id', async (req: express.Request, res: express.Response, next:express.NextFunction) => {
  const messageId = req.params.id
  const message = req.body as Message;
  const saved = await userService.update(messageId, message).catch(next);
  res.status(200).json(saved);
});
