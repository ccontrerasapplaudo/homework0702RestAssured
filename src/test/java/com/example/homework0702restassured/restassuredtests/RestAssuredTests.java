package com.example.homework0702restassured.restassuredtests;


import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class RestAssuredTests {

    String token;
    String roomID;

    @Test(priority = 1)
    public void getRoom() {
        String id = "1";
        given()
                .baseUri("https://automationintesting.online").
                pathParam("id", id).
                when()
                .get("/room/{id}").
                then()
                .log().body();
    }

    @Test(priority = 2)
    public void postRetrieveToken() {
        File file = new File("resources/authCredentials.json");
        Map<String, String> cookies = given()
                .baseUri("https://automationintesting.online")
                .contentType(ContentType.JSON)
                .body(file).
                when()
                .post("/auth/login").
                then()
                .statusCode(200)
                .extract().cookies();
        System.out.println("Token value: " + cookies.get("token"));
        token = cookies.get("token");
    }

    @Test(priority = 3)
    public void getRoom1N() {
        getAllRooms();
    }

    @Test(priority = 4)
    public void getRoomIDTest() {
        getRoomID();
    }

    @Test(priority = 5)
    public void postBooking1Room() throws IOException, ParseException {
        FileReader reader = new FileReader("resources/booking1Room.json");
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
        jsonObject.put("roomid", roomID);
        Cookie cookie = new Cookie.Builder("token", token).setSecured(true).setComment("Booking 1 room cookie").build();
        given()
                .baseUri("https://automationintesting.online")
                .cookie(cookie)
                .contentType(ContentType.JSON).
                body(jsonObject.toString()).
                when()
                .post("/booking/").
                then()
                .statusCode(201).
                log().all();
    }

    @Test(priority = 6)
    public void getRoom1NSecondCall() {
        getAllRooms();
    }

    @Test(priority = 7)
    public void getRoomIDTestSecondCall() {
        getRoomID();
    }

    @Parameters({"firstname","lastname","checkin","checkout"})
    @Test(priority = 8)
    public void postBooking1RoomWithParameters(String firstname, String lastname, String checkin, String checkout) throws IOException, ParseException {
        FileReader reader = new FileReader("resources/booking1Room.json");
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
        jsonObject.put("roomid", roomID);
        jsonObject.put("firstname", firstname);
        jsonObject.put("lastname", lastname);
        JSONObject checking = (JSONObject) jsonObject.get("bookingdates");
        checking.put("checkin", checkin);
        checking.put("checkout", checkout);
        Cookie cookie = new Cookie.Builder("token", token).setSecured(true).setComment("Booking 1 room cookie").build();
        given()
                .baseUri("https://automationintesting.online")
                .cookie(cookie)
                .contentType(ContentType.JSON).
                body(jsonObject.toString()).
                when()
                .post("/booking/").
                then()
                .statusCode(201).
                log().all();
    }

    public void getAllRooms(){
        given()
                .baseUri("https://automationintesting.online").
                when()
                .get("/room/").
                then()
                .log().body();
    }

    public void getRoomID(){
        Response response = given()
                .baseUri("https://automationintesting.online").
                when()
                .get("/room/");

        JsonPath jsonPath = new JsonPath(response.asString());

        int min = 0;
        int max = jsonPath.getInt("rooms.size()")-1;
        int randomResult = (int)(Math.random()*(max-min+1)+min);
        int index = randomResult;

        String path = "rooms"+"["+index+"]"+".roomid";
        System.out.println("Selected ID to perform booking "+jsonPath.getString(path));
        roomID = jsonPath.getString(path);
    }

}
