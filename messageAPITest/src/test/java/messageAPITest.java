import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.notNullValue;


@TestMethodOrder(OrderAnnotation.class)
public class messageAPITest {

    private static String messageId;
    private static String time;
    private static final String BASE_URL = "http://localhost:3000";
    private static final String FROM_USER_ID = "ed0c4df0-2161-4bf4-bde3-a39184f1892b";
    private static final String TO_USER_ID = "c4dfea2e-d4a5-49b8-a814-42501d258367";
    private static final String INVALID_USER_ID = "c4dfea2e-d4a5-0000-a814-42501d258367";
    private static final String NON_EXISTENT_MESSAGE_ID = "4f98c5ad-88c2-4ef8-8058-32d154a6v555";

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    @Order(1)
    public void testCreateMessage() {
        String messagePayload = "{"
        		 + "\"from\": {\"id\": \"" + FROM_USER_ID + "\"},"
                 + "\"to\": {\"id\": \"" + TO_USER_ID + "\"},"
                 + "\"message\": \"Creating test message sample\""
                // + "\"id\": \"uuid-of-message\","
               //  + "\"time\": \"2021-03-04T00:54:30.288Z\""
                 + "}";

        Response response = given()
            .header("Content-Type", "application/json")
            .body(messagePayload)
            .post("/api/messages");
        
        if (response.statusCode() != 200) {
            System.out.println("Error response: " + response.getBody().asString());
        }
        response.then().statusCode(200);
        response.then().body("from.id", equalTo(FROM_USER_ID));
        response.then().body("to.id", equalTo(TO_USER_ID));
        response.then().body("message", equalTo("Creating test message sample"));

        messageId = response.jsonPath().getString("id");
        time = response.jsonPath().getString("time");
        System.out.println("messageid: "+messageId);
        System.out.println("time: "+time);
    }

    @Test
    @Order(2)
    public void testGetMessagesBetweenUsers() {
            Response response = given()
                .queryParam("from", FROM_USER_ID)
                .queryParam("to", TO_USER_ID)
                .get("/api/messages");

            if (response.statusCode() != 200) {
                System.out.println("Error response: " + response.getBody().asString());
            }

            response.then().statusCode(200);
            response.then().body("$", not(empty())); 
            response.then().body("[0].from.id", equalTo(FROM_USER_ID));
            response.then().body("[0].to.id", equalTo(TO_USER_ID));
            response.then().body("[0].message", notNullValue());
    }

    @Test
    @Order(3)
    public void testGetMessageById() {
        Response response = given()
        		.header("Content-Type", "application/json")
            .get("/api/messages/" + messageId);

        response.then().statusCode(200);
        response.then().body("message", equalTo("Creating test message sample"));
    }



    
// Defect test will fail
    @Test
    @Order(4)
    public void testGetMessagesBetweenUsers_NoMessages() {
        Response response = given()
            .queryParam("from", FROM_USER_ID)
            .queryParam("to", INVALID_USER_ID)
            .get("/api/messages");
          response.then().statusCode(404);
          response.then().body("", hasSize(0));
    }
    
  // Defect test will fail
    
    @Test
    @Order(5)
    public void testGetMessagesBetweenUsers_NoMessages01() {
        Response response = given()
            .queryParam("from", INVALID_USER_ID)
            .queryParam("to", TO_USER_ID  )
            .get("/api/messages");
        response.then().statusCode(404);
        response.then().body("", hasSize(0));
    }

   // doesnt validate the error message since no response body is present 
    @Test
    @Order(6)
    public void testGetMessageById_NonExistent() {
        Response response = given()
            .get("/api/messages" + NON_EXISTENT_MESSAGE_ID);

        response.then().statusCode(404);
    }

    
    //doesnt validate the error name and message
    @Test
    @Order(7)
    public void testCreateMessage_MissingField() {
        String invalidMessagePayload = "{"
                + "\"from\": {\"id\": \"" + FROM_USER_ID + "\"},"
                + "\"message\": \"missing field message\""
                + "}";

        Response response = given()
            .header("Content-Type", "application/json")
            .body(invalidMessagePayload)
            .post("/api/messages");

        System.out.println("Actual response: " + response.getBody().asString());
        response.then().statusCode(400);
       // response.then().body("name", equalTo("ServiceError"));
       //response.then().body("message", equalTo("Missing required fields"));
    }

    @Test
    @Order(8)
    public void testUpdateMessageById() {
  	  assertNotNull(messageId, "Message ID should not be null for update test");

        String updatedMessagePayload =   "{"
        + "\"message\": \"Updated message\""
        + "}";
        
        Response Updatedresponse = given()
            .header("Content-Type", "application/json")
            .body(updatedMessagePayload)
            .put("/api/messages/:id" + messageId);
       
        // Print the response body for debugging purposes
        System.out.println("Actual response: " + Updatedresponse.getBody().asString());
        
        if (Updatedresponse.statusCode() != 200) {
            System.out.println("Error response: " + Updatedresponse.getBody().asString());
        }

        Updatedresponse.then().statusCode(200);
    }
    
    @Test
    @Order(9)
    public void testUpdateMessageById_NonExistent() {
  	  assertNotNull(NON_EXISTENT_MESSAGE_ID, "Message ID should not be null for update test");

  	 String updatedMessagePayload =   "{"
  	        + "\"message\": \"Updated message\""
  	        + "}";
  	 
  	Response Updatedresponse = given()
            .header("Content-Type", "application/json")
            .body(updatedMessagePayload)
            .put("/api/messages/:id" + NON_EXISTENT_MESSAGE_ID);
  	
  	if (Updatedresponse.statusCode() != 404) {
        System.out.println("Error response: " + Updatedresponse.getBody().asString());
    }
        Updatedresponse.then().statusCode(404);
    }

    @Test
    @Order(10)
    public void testDeleteMessageById() {
    	assertNotNull(messageId, "Message ID should not be null for delete test");
    	System.out.println(messageId);
        Response response = given()
            .delete("/api/messages/" + messageId);
        response.then().statusCode(204);
    }
    
    @Test
    @Order(11)
    public void testDeleteMessageById_NonExistent() {
        Response response = given()
            .delete("/api/messages/" + NON_EXISTENT_MESSAGE_ID);

        response.then().statusCode(404);
    }
    

    
    
    
  
    

}
