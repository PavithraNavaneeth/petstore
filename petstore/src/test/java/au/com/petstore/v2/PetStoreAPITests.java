package au.com.petstore.v2;

import au.com.petstore.v2.user.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class PetStoreAPITests {
    private static final String BASE_URL = "https://petstore.swagger.io/v2";

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    @Order(1)
    public void testAddNewPet() {
        String requestBody = "{ \"id\": 999, \"name\": \"Doggie\", \"category\": { \"id\": 0, \"name\": \"Dog\" }, \"photoUrls\": [\"url1\"], \"tags\": [{ \"id\": 0, \"name\": \"tag1\" }], \"status\": \"available\" }";

        given().
                contentType(ContentType.JSON).
                body(requestBody).
                when().
                    post("/pet/").
                then()
                    .statusCode(200)
                    .body("name", equalTo("Doggie"))
                    .body("status", equalTo("available"));
    }

    @Test
    @Order(2)
    public void testUploadImage() {
        int petId = 999;
        String imagePath = new File(getClass().getClassLoader().getResource("image.jpg").getFile()).getPath();

        String response = given()
                .multiPart("file", imagePath)
                .when()
                .post("/pet/{petId}/uploadImage", petId)
                .then()
                .statusCode(200)
                .body("", hasKey("message"))
                .extract().asString();

        System.out.println("Response: " + response);
    }

    @Test
    @Order(3)
    public void testGetPetById() {
        int petId = 999;
        given()
                .when()
                .get("/pet/{petId}", petId)
                .then()
                .statusCode(200)
                .body("id", equalTo(petId));
    }

    @Test
    @Order(4)
    public void testGetPetsByStatus() {
        given()
                .queryParam("status", "available")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .body("$", is(not(empty())));
    }

    @Test
    @Order(5)
    public void testUpdatePet() {
        String requestBody = "{ \"id\": 999, \"name\": \"Doggie Updated\", \"category\": { \"id\": 0, \"name\": \"Dog\" }, \"photoUrls\": [\"url1\"], \"tags\": [{ \"id\": 0, \"name\": \"tag1\" }], \"status\": \"available\" }";

        given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                    .put("/pet")
                .then()
                    .statusCode(200)
                    .body("name", equalTo("Doggie Updated"));
    }

    @Test
    @Order(6)
    public void testUpdatePetWithFormData() {
        int petId = 999;

        String petName = "Puppy";
        String petStatus = "available";

        given()
                .formParam("name", petName)
                .formParam("status", petStatus)
                .when()
                    .post("/pet/{petId}", petId)
                .then()
                    .statusCode(200);
    }

    @Test
    @Order(7)
    public void testDeletePet() {
        int petId = 999;
        given()
                .when()
                    .delete("/pet/{petId}", petId)
                .then()
                    .statusCode(200);
    }

    @Test
    @Order(8)
    public void testGetStoreInventory() {
        given()
                .when()
                    .get("/store/inventory")
                .then()
                    .statusCode(200)
                    .body("", hasKey("available"));
    }

    @Test
    @Order(9)
    public void testPlaceOrder() {
        String requestBody = "{ \"id\": 999, \"petId\": 1, \"quantity\": 1, \"shipDate\": \"2024-01-01T00:00:00.000Z\", \"status\": \"placed\", \"complete\": true }";

        given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                    .post("/store/order")
                .then()
                    .statusCode(200)
                    .body("status", equalTo("placed"));
    }

    @Test
    @Order(10)
    public void testGetOrderById() {
        int orderId = 999;
        given()
                .when()
                    .get("/store/order/{orderId}", orderId)
                .then()
                    .statusCode(200)
                    .body("id", equalTo(orderId));
    }

    @Test
    @Order(11)
    public void testDeleteOrder() {
        int orderId = 999;
        given()
                .when()
                    .delete("/store/order/{orderId}", orderId)
                .then()
                    .statusCode(200);
    }

    @Test
    @Order(12)
    public void testCreateUserWithList() {
        List<User> users = Arrays.asList(
                new User(997,"john_doe", "John", "Doe", "john@example.com", "123456789", "admin", 0),
                new User(998, "jane_doe", "Jane", "Doe", "jane@example.com", "987654321", "user", 1)
        );

        given()
                .contentType("application/json")
                .body(users)
                .when()
                    .post("/user/createWithList")
                .then()
                    .statusCode(200)
                    .body("message", equalTo("ok"));
    }

    @Test
    @Order(13)
    public void testGetUserByUsername() {
        String username = "john_doe";
        given()
                .when()
                .get("/user/{username}", username)
                .then()
                .statusCode(200)
                .body("username", equalTo(username));
    }

    @Test
    @Order(14)
    public void testUpdateUser() {
        String requestBody = "{ \"id\": 997, \"username\": \"john_doe\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john@example.com\", \"password\": \"123456789\", \"phone\": \"047689452\", \"userStatus\": 0 }";
        String username = "john_doe";
        given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .put("/user/{username}",username)
                .then()
                .statusCode(200)
                .body("message", equalTo("997"));
    }

    @Test
    @Order(15)
    public void testDeleteUser() {
        String username = "jane_doe";
        given()
                .when()
                .delete("/user/{username}", username)
                .then()
                .statusCode(200);
    }

    @Test
    @Order(16)
    public void testCreateUserWithArray() {
        List<User> users = Arrays.asList(
                new User(995,"ben_doe", "Ben", "Doe", "ben@example.com", "123456789", "0678965432", 0),
                new User(996, "den_doe", "Den", "Doe", "den@example.com", "987654321", "0465789345", 1)
        );

        given()
                .contentType("application/json")
                .body(users)
                .when()
                .post("/user/createWithArray")
                .then()
                .statusCode(200)
                .body("message", equalTo("ok"));
    }

    @Test
    @Order(17)
    public void testCreateUser() {
        String requestBody = "{ \"id\": 994, \"username\": \"lilly_doe\", \"firstName\": \"Lilly\", \"lastName\": \"Doe\", \"email\": \"lilly@example.com\", \"password\": \"1231242134214\", \"phone\": \"0765432879\", \"userStatus\": 0 }";

        given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/user")
                .then()
                .statusCode(200)
                .body("message", equalTo("994"));
    }

    @Test
    @Order(18)
    public void testUserLogin() {
        String username = "john_doe";
        String password = "123456789";

        given()
                .queryParam("username", username)
                .queryParam("password", password)
                .when()
                .get("/user/login")
                .then()
                .statusCode(200) // Expect a successful response
                .body("", hasKey("message"));
    }

    @Test
    @Order(19)
    public void testUserLogout() {
        given()
                .when()
                .get("/user/logout")
                .then()
                .statusCode(200)
                .body("message", equalTo("ok"));
    }
}
