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

import java.util.*;

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

        assertNotNull(cookie);
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

    // Занятие №3. ДЗ 4. Ex13: User Agent. Не параметризованный тест.
    @Test
    public void userAgent(){
        String[] userAgentsStrings =
                {"Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
                "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1",
                "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0",
                "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1"};

        SortedMap<String, String> userAgents = new TreeMap<>();
        for (int i = 0; i < userAgentsStrings.length; i++) {
            userAgents.put("User Agent " + i, userAgentsStrings[i]);
        }
        //userAgents.forEach((k,v) -> System.out.println(k + ": " + v));

        String userAgentCheckUrl = "https://playground.learnqa.ru/ajax/api/user_agent_check";

        SortedMap<Integer, SortedMap<String, String>> resultAnswer = new TreeMap<>();

        for (int i = 0; i < userAgentsStrings.length; i++) {
            Map<String, String> queryParam = new HashMap<>();
            queryParam.put("User Agent", userAgentsStrings[i]);

            JsonPath response = RestAssured
                    .given()
                    .queryParams(queryParam)
                    .get(userAgentCheckUrl)
                    .jsonPath();

            //response.prettyPrint();

            SortedMap<String, String> answer = new TreeMap<>();
            answer.put("user_agent", response.getString("user_agent"));
            answer.put("platform", response.getString("platform"));
            answer.put("browser", response.getString("browser"));
            answer.put("device", response.getString("device"));

            resultAnswer.put(i, answer);

        }
        //resultAnswer.forEach((k,v) -> System.out.println(k + ": " + v));

        System.out.println("Список User Agent, которые вернули неправильным хотя бы один параметр, с указанием того, какой именно параметр неправильный.");

        Iterator<SortedMap.Entry<Integer, SortedMap<String, String>>> itr =  resultAnswer.entrySet().iterator();
        while(itr.hasNext()) {
            SortedMap.Entry<Integer, SortedMap<String, String>> entry =  itr.next();
            Integer key = entry.getKey();
            SortedMap<String, String> value = entry.getValue();

            System.out.println();
            System.out.println("№ User Agent: " + key);

            for(SortedMap.Entry<String, String> entryValue: value.entrySet()) {
                String Key = entryValue.getKey();
                String Value = entryValue.getValue();

                // Список User Agent, которые вернули неправильным хотя бы один параметр, с указанием того, какой именно параметр неправильный.

                if (Key.equals("platform") & (Value.equals("Mobile") | Value.equals("Web"))){
                    System.out.println("Параметр: " + Key + " принимает правильное значение.");
                } else if (Key.equals("browser") & (Value.equals("Chrome") | Value.equals("Firefox") | Value.equals("Yandex"))) {
                    System.out.println("Параметр: " + Key + " принимает правильное значение.");
                }else if (Key.equals("device") & (Value.equals("Android") | Value.equals("iOS"))){
                    System.out.println("Параметр: " + Key + " принимает правильное значение.");
                }

                if (Key.equals("platform") & (Value.equals("Unknown") | Value.equals("No") | Value.equals("Googlebot"))){
                    System.out.println("Параметр: " + Key + " принимает Не правильное значение.");
                } else if (Key.equals("browser") & (Value.equals("Unknown") | Value.equals("No"))) {
                    System.out.println("Параметр: " + Key + " принимает Не правильное значение.");
                }else if (Key.equals("device") & (Value.equals("Unknown") | Value.equals("No")| Value.equals("iPhone"))){
                    System.out.println("Параметр: " + Key + " принимает Не правильное значение.");
                }

                if (Key.equals("user_agent")){
                    System.out.println("User Agent: " + Key + ", имеет значение: " + Value);
                }


            }

        }

    }


}
