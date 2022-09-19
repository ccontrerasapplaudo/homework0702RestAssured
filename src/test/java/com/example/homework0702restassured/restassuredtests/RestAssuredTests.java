package com.example.homework0702restassured.restassuredtests;


import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

import java.io.File;
import java.util.Map;

public class RestAssuredTests {

    @Test(priority = 1)
    public void getRoom() {
        given()
                .baseUri("https://automationintesting.online").
                when()
                .get("/room/").
                then()
                .log().body();
    }

    @Test(priority = 2)
    public void postRetrieveToken() {
        File file = new File("resources/step01/authCredentials.json");
        Map<String,String> cookies = given()
                .baseUri("https://automationintesting.online")
                .contentType(ContentType.JSON)
                .body(file).
                when()
                .post("/auth/login").
                then()
                .statusCode(200)
                .extract().cookies();
        System.out.println("Token value: "+cookies.get("token"));
    }

}
