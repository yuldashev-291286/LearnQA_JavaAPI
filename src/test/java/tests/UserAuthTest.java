package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.BaseTestCase;
import lib.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.*;

public class UserAuthTest extends BaseTestCase {

    String cookie;
    String header;
    int userIdOnAuth;

    @BeforeEach
    public void loginUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth, "user_id");

    }

    // Занятие №3, Учебный тест №6
    @Test
    public void testAuthUser(){

        JsonPath responseCheckAuth = RestAssured
                .given()
                .header("x-csrf-token", this.header)
                .cookie("auth_sid", this.cookie)
                .get("https://playground.learnqa.ru/api/user/auth")
                .jsonPath();

        int userIdOnCheck = responseCheckAuth.getInt("user_id");
        assertTrue(userIdOnCheck > 0, "Unexpected user id" + userIdOnCheck);

        assertEquals(
                userIdOnAuth,
                userIdOnCheck,
                "User id from auth request is not equal to user_id from check request"
        );

    }

    // Занятие №3, Учебный тест №7
    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition){
        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");

        if (condition.equals("cookie")){
            spec.cookie("auth_sid", this.cookie);
        } else if (condition.equals("headers")) {
            spec.header("x-csrf-token", this.header);
        }else {
            throw new IllegalArgumentException("Condition value is known: " + condition);
        }

        JsonPath responseForCheck = spec.get().jsonPath();
        assertEquals(0, responseForCheck.getInt("user_id"), "user_id should be 0 for unauth request");


    }

    // Занятие №3, Учебный тест №8
    @Test
    public void testAuthUser1(){

        Response responseCheckAuth = RestAssured
                .given()
                .header("x-csrf-token", this.header)
                .cookie("auth_sid", this.cookie)
                .get("https://playground.learnqa.ru/api/user/auth")
                .andReturn();

        Assertions.asserJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
    }

    // Занятие №3, Учебный тест №9
    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser1(String condition){
        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");

        if (condition.equals("cookie")){
            spec.cookie("auth_sid", this.cookie);
        } else if (condition.equals("headers")) {
            spec.header("x-csrf-token", this.header);
        }else {
            throw new IllegalArgumentException("Condition value is known: " + condition);
        }

        Response responseForCheck = spec.get().andReturn();
        Assertions.asserJsonByName(responseForCheck, "user_id", 0);

    }

    // Занятие №3. ДЗ 1. Ex10: Тест на короткую фразу. Фреймворк JUnit.
    @Test
    public void testShortPhraseJUnit(){
        String hello = "Hello, world !!!";
        int sizeStringHello = hello.length();

        assertTrue(sizeStringHello > 15, "Длина строки hello больше 15 символов");
        //assertFalse(sizeStringHello <= 15, "Длина строки hello меньше или равно 15 символов");
    }

    // Занятие №3. ДЗ 1. Ex10: Тест на короткую фразу. Библиотека Hamcrest.
    @Test
    public void testShortPhraseHamcrest(){
        String hello = "Hello, world !!!";
        int sizeStringHello = hello.length();
        assertThat(sizeStringHello, allOf(greaterThan(15)));
    }

    // Занятие №3. ДЗ 2. Ex11: Тест запроса на метод cookie.
    @Test
    public void testRequestForCookieMethod(){

        // Доступы к предустановленному пользователю:
        String email = "vinkotov@example.com";
        String password = "1234";

        String cookieUrl = "http://playground.learnqa.ru/api/homework_cookie";

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("email", email);
        queryParams.put("password", password);

        JsonPath response = RestAssured
                .given()
                .queryParams(queryParams)
                .post(cookieUrl)
                .jsonPath();
        //response.prettyPrint();

        String cookie = response.toString();
        System.out.println(cookie);

        assertNotNull(response);
        //assertNull(cookie);

    }

    // Занятие №3. ДЗ 3. Ex12: Тест запроса на метод header.
    @Test
    public void testRequestForHeaderMethod(){

        // Доступы к предустановленному пользователю:
        String email = "vinkotov@example.com";
        String password = "1234";

        String headerUrl = "https://playground.learnqa.ru/api/homework_header";

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("email", email);
        queryParams.put("password", password);

        JsonPath response = RestAssured
                .given()
                .queryParams(queryParams)
                .post(headerUrl)
                .jsonPath();
        response.prettyPrint();

        String header = response.getString("success");

        assertNotNull(header);
        //assertNull(header);

    }



}
