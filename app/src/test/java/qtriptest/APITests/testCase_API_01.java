package qtriptest.APITests;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ResponseBody;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.RestAssured;
import java.util.UUID;

import static org.hamcrest.Matchers.*;


@SuppressWarnings("unused")
public class testCase_API_01{

    RequestSpecification http;
    public static String email;
    public static String password;
    public static String token;
    public static String userId;
    Response response;


    @BeforeMethod
    public void setup(){
        RestAssured.baseURI = "https://content-qtripdynamic-qa-backend.azurewebsites.net";
        RestAssured.basePath = "/api/v1/";
        http = RestAssured.given();
    }


    @Test(description = "Verify that a user can be registered and login using APIs of QTrip", priority = 1, groups = "API Tests")
    public void TestCase01(){
        //Register 
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

        Assert.assertEquals(statusCode , 201, "ERROR: status code is not 201");
        Assert.assertTrue(success , "ERROR: while validating the success message is true");

        // response.then().assertThat().statusCode(201);
        // response.then().assertThat().body("success", equalTo(true));

        //Login
        jsonObj = new JSONObject();
        jsonObj.put("email", email);
        jsonObj.put("password", password);

        response = http
                        .contentType("application/json")
                        .body(jsonObj.toString())
                    .when()
                        .post("login");

        statusCode = response.getStatusCode();
        token = response.body().jsonPath().get("data.token");
        userId = response.body().jsonPath().getString("data.id");
        success = response.body().jsonPath().get("success");

        Assert.assertEquals(statusCode, 201);
        Assert.assertTrue(success);
        Assert.assertNotNull(token);
        Assert.assertNotNull(userId);

        // response.then().assertThat().statusCode(201);
        // response.then().body("success", equalTo(true));
        // response.then().body("data.token", notNullValue());
        // response.then().log().all().body("data.id", notNullValue());

    }
    
}
