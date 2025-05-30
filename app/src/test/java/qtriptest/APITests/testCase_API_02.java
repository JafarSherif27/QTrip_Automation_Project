package qtriptest.APITests;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
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
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class testCase_API_02 {

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


    @Test(description = "Verify that the search City API Returns the correct number of results", priority = 2, groups = "API Tests")
    public void TestCase02(){

        response = http
                        .queryParam("q", "beng")
                    .when()
                        .get("cities");
                            
        List<HashMap<String, String>> listOfCities = response.body().jsonPath().get("$"); 
        // System.out.println("List: "+listOfCities);


        int statusCode = response.getStatusCode();
        int noOfCitiesReturned = listOfCities.size();
        String description = listOfCities.get(0).get("description");
        String expectedDescription = "100+ Places";
        // String description = response.body().jsonPath().getString("[0].description");
        // System.out.println("des: "+des);

        Assert.assertEquals(statusCode , 200 );
        Assert.assertEquals(noOfCitiesReturned,1);
        Assert.assertEquals(description, expectedDescription);


        // response.then().assertThat().statusCode(200);
        // response.then().assertThat().body("size()", equalTo(1));
        // response.then().assertThat().body("[0].description", equalTo("100+ Places"));


        //schema validation 
        File schemaFile = new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"Schema.json");
        JsonSchemaValidator matcher = JsonSchemaValidator.matchesJsonSchema(schemaFile);
        response.then().assertThat().body(matcher);


    }






}
