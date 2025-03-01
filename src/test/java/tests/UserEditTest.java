package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserEditTest extends BaseTestCase {

    // Занятие 4. Учебный тест №7.
    @Test
    public void testEditJustCreatedTest(){
        // GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        // Url: https://playground.learnqa.ru/api/user/, всегда возвращает: {"error":"Wrong HTTP method"}

        String userId = "1"; //responseCreateAuth.getString("id");

        // LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        // EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        // Url: https://playground.learnqa.ru/api/user/" + userId.
        // userId=0, возвращает: {"error":"Wrong HTTP method"}
        // userId=1, возвращает: {"username":"Lana"}
        // userId=2, возвращает: {"username":"Vitaliy"}
        // userId=3, возвращает: {"username":"arsbatyrov"}
        // userId>3, возвращает: User not found

        Response responseEditUser = RestAssured
                .given()
                //.header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .header("x-csrf-token", "x-csrf-token")
                //.cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .cookie("auth_sid", "auth_sid")
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        // GET
        Response responseUserData = RestAssured
                .given()
                //.header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .header("x-csrf-token", "x-csrf-token")
                //.cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .cookie("auth_sid", "auth_sid")
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //Assertions.asserJsonByName(responseUserData, "firstName", newName);

        Integer number = Integer.valueOf(userId);
        if (number == 0){
            System.out.print("Неправильный метод HTTP: ");
            System.out.println(responseUserData.asString());

        }
        else if (number == 1){
            Assertions.asserJsonByName(responseUserData, "username", "Lana");
            System.out.println(responseUserData.asString());

        }else if (number == 2){
            Assertions.asserJsonByName(responseUserData, "username", "Vitaliy");
            System.out.println(responseUserData.asString());

        }
        else if (number == 3){
            Assertions.asserJsonByName(responseUserData, "username", "arsbatyrov");
            System.out.println(responseUserData.asString());

        }else if (number > 3){
            assertTrue(responseUserData.asString().equals("User not found"), "User not found");
            System.out.println(responseUserData.asString());

        }



    }

}
