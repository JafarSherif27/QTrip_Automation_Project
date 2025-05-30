package qtriptest.APITests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.*;

import java.util.UUID;


@SuppressWarnings("unused")
public class testCase_API_04 {
    RequestSpecification http;
    public static String email;
    public static String password;
    public static String token;
    public static String userId;
    Response response;


    @BeforeMethod
    public void setup() {
        RestAssured.baseURI = "https://content-qtripdynamic-qa-backend.azurewebsites.net";
        RestAssured.basePath = "/api/v1/";
        http = RestAssured.given();
    }



    @Test(description = "Verify duplicate user account cannot be created on the Qtrip Website",
            priority = 4, groups = "API Tests")
    public void TestCase04() {

        // Register
        email = String.format("test%s@gmail.com", UUID.randomUUID().toString());
        password = String.format("testUser%s", UUID.randomUUID().toString());


        JSONObject jsonObj = new JSONObject();
        jsonObj.put("email", email);
        jsonObj.put("password", password);
        jsonObj.put("confirmpassword", password);

        response = http
                        .header("Content-Type", "application/json")
                        .body(jsonObj.toString())
                    .when()
                        .post("register");

        int statusCode = response.getStatusCode();
        boolean success = response.body().jsonPath().get("success");

        Assert.assertEquals(statusCode, 201, "ERROR: status code is not 201");
        Assert.assertTrue(success, "ERROR: while validating the success message is true");

        //response.then().assertThat().statusCode(201).log().all();
        // response.then().assertThat().body("success", equalTo(true));

        // Re-Register with same email and password
        response = http
                        .header("Content-Type", "application/json")
                        .body(jsonObj.toString())
                    .when()
                        .post("register");

        statusCode = response.getStatusCode();
        success = response.body().jsonPath().get("success");
        String message = response.body().jsonPath().get("message");

        Assert.assertEquals(statusCode, 400, "ERROR: status code is not 400");
        Assert.assertFalse(success, "ERROR: while validating the success message is false");
        Assert.assertEquals(message, "Email already exists");

        // response.then().assertThat().statusCode(400);
        // response.then().assertThat().body("success", equalTo(false));
        // response.then().assertThat().body("message", equalTo("Email already exists")).log().all();


    }


}


