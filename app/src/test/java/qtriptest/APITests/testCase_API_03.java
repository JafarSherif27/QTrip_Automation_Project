package qtriptest.APITests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.openqa.selenium.json.Json;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


@SuppressWarnings("unused")
public class testCase_API_03{

    RequestSpecification http;
    public static String email;
    public static String password;
    public static String token;
    public static String userId;
    public static String adventureId = "1248029271";
    public static String reservationId;
    Response response;


    @BeforeMethod
    public void setup(){
        RestAssured.baseURI = "https://content-qtripdynamic-qa-backend.azurewebsites.net";
        RestAssured.basePath = "/api/v1/";
        http = RestAssured.given();
    }


    @Test(description = "Verify reservation can be made using the QTrip API", priority = 3, groups = "API Tests")
    public void TestCase03(){

        //Register 
        //System.out.println("Register........");

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

        // response.then().assertThat().statusCode(201).log().all();
        // response.then().assertThat().body("success", equalTo(true));


        //Login
        //System.out.println("\nLogin.......");

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

        // Making reservation
        // System.out.println("\nMake new reservation......");

        jsonObj =  new JSONObject();
        jsonObj.put("userId", userId);
        jsonObj.put("name", "testuser");
        jsonObj.put("date", "2025-12-09");
        jsonObj.put("person", 3);
        jsonObj.put("adventure", adventureId);
     

        //registering - post make new reservation
        response = http
                        .contentType("application/json")
                        .header("Authorization", "Bearer "+token)
                        .body(jsonObj.toString())
                    .when()
                        .post("reservations/new");

        Assert.assertEquals(response.getStatusCode(), 200);

       // response.then().assertThat().statusCode(200).log().all();
        

        //checking if the registration is make successfully  - get reservation made
      //  System.out.println("\nCheck registration is successful..... ");

        RequestSpecification httpRequest = RestAssured.given();
        response = httpRequest
                        .queryParam("id", userId)
                        .header("Authorization", "Bearer "+token)
                    .when()
                        .get("reservations/"); //

                    

        // System.out.println(response.getBody().asString());

        List<HashMap<String, Object>> listOfReservations = response.body().jsonPath().get("$");
        boolean isReservationSuccess = false; 

        for(int i=0; i<listOfReservations.size(); i++){
            String currentAdventureID = (String) listOfReservations.get(i).get("adventure");

            // System.out.println("currentAdventureID: "+ currentAdventureID);
            if(currentAdventureID.equals(adventureId)){
                reservationId = (String) listOfReservations.get(i).get("id");
                isReservationSuccess = true;
                break;
            }
        }


       statusCode = response.getStatusCode();

       Assert.assertEquals(statusCode, 200);
       Assert.assertTrue(isReservationSuccess);

       //response.then().log().all().assertThat().statusCode(200);


    }




}
