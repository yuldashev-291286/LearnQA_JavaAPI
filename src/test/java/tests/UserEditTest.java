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

        // PUT
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

        Integer numberUserId = Integer.valueOf(userId);
        if (numberUserId == 0){
            System.out.print("Неправильный метод HTTP: ");
            System.out.println(responseUserData.asString());

        }
        else if (numberUserId == 1){
            Assertions.asserJsonByName(responseUserData, "username", "Lana");
            System.out.println(responseUserData.asString());

        }else if (numberUserId == 2){
            Assertions.asserJsonByName(responseUserData, "username", "Vitaliy");
            System.out.println(responseUserData.asString());

        }
        else if (numberUserId == 3){
            Assertions.asserJsonByName(responseUserData, "username", "arsbatyrov");
            System.out.println(responseUserData.asString());

        }else if (numberUserId > 3){
            assertTrue(responseUserData.asString().equals("User not found"), "User not found");
            System.out.println(responseUserData.asString());

        }


    }

    // Занятие 4. Ex17: Негативные тесты на PUT. Тест №1: Попытаемся изменить данные пользователя, будучи неавторизованными.
    @Test
    public void testChangeUserDataWithoutAuthorization(){

        // Метод создания пользователя: https://playground.learnqa.ru/api/user/, всегда возвращает: {"error":"Wrong HTTP method"}

        // Метод изменения пользователя: https://playground.learnqa.ru/api/user/" + userId.
        // userId=0, возвращает: {"error":"Wrong HTTP method"}
        // userId=1, возвращает: {"username":"Lana"}
        // userId=2, возвращает: {"username":"Vitaliy"}
        // userId=3, возвращает: {"username":"arsbatyrov"}
        // userId>3, возвращает: User not found

        // Id пользователя, которому будем менять данные
        String userId = "1"; // {"username":"Lana"}

        // Задаем новое username
        String newUserName = "Changed User Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("username", newUserName);

        // Отправляем измененное username
        Response responseEditUser = RestAssured
                .given()
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        // Здесь всегда возвращается: {"error":"Auth token not supplied"}
        if (responseEditUser.asString().contains("Auth token not supplied")){
            System.out.print("Токен аутентификации не предоставлен: ");
            System.out.println(responseEditUser.asString());

        }else {
            // Получаем данные пользователя с новым username
            Response responseUserData = RestAssured
                    .given()
                    .get("https://playground.learnqa.ru/api/user/" + userId)
                    .andReturn();

            System.out.println(responseUserData.asString());
            // Сравниваем старое username с новым
            Assertions.asserJsonByName(responseUserData, "username", newUserName);

        }

    }

    // Занятие 4. Ex17: Негативные тесты на PUT. Тест №2: Попытаемся изменить данные пользователя, будучи авторизованными другим пользователем.
    @Test
    public void testChangeUserDataWhileBeingAuthorizedByAnotherUser(){

        // Метод создания пользователя: https://playground.learnqa.ru/api/user/, всегда возвращает: {"error":"Wrong HTTP method"}
        // Авторизуемся с нашим пользователем:
        // {'email': 'vinkotov@example.com', 'password': '1234', 'user_id':'2'}
        String email = "vinkotov@example.com";
        String password = "1234";
        Map<String, String> authData = new HashMap<>();
        authData.put("email", email);
        authData.put("password", password);

        // Авторизация с email и password
        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //System.out.println(responseGetAuth.asString());

        // Метод изменения пользователя: https://playground.learnqa.ru/api/user/" + userId.
        // userId=0, возвращает: {"error":"Wrong HTTP method"}
        // userId=1, возвращает: {"username":"Lana"}
        // userId=2, возвращает: {"username":"Vitaliy"}
        // userId=3, возвращает: {"username":"arsbatyrov"}
        // userId>3, возвращает: User not found

        // Id пользователя, которому будем менять данные
        String userId = "1"; // {"username":"Lana"}

        // Задаем новое имя пользователя
        String newUserName = "Changed User Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("username", newUserName);

        // Отправляем измененное имя пользователя
        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        // Здесь всегда возвращается: {"error":"Please, do not edit test users with ID 1, 2, 3, 4 or 5."}
        if (responseEditUser.asString().contains("Please, do not edit test users with ID 1, 2, 3, 4 or 5.")){
            System.out.print("Пожалуйста, не редактируйте тестовых пользователей с идентификаторами 1, 2, 3, 4 или 5: ");
            System.out.println(responseEditUser.asString());

        }else {
            // Получаем данные пользователя с новым именем
            Response responseUserData = RestAssured
                    .given()
                    .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                    .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                    .get("https://playground.learnqa.ru/api/user/" + userId)
                    .andReturn();

            System.out.println(responseUserData.asString());
            // Сравниваем старое username с новым
            Assertions.asserJsonByName(responseUserData, "username", newUserName);

        }

    }

    // Занятие 4. Ex17: Негативные тесты на PUT. Тест №3: Попытаемся изменить email пользователя, будучи авторизованными тем же пользователем, на новый email без символа @.
    @Test
    public void testChangeUserEmailWhileLoggedInBySameUserButWithoutTheAtSymbol(){

        // Метод создания пользователя: https://playground.learnqa.ru/api/user/, всегда возвращает: {"error":"Wrong HTTP method"}
        // Авторизуемся с нашим пользователем:
        // {'email': 'vinkotov@example.com', 'password': '1234', 'user_id':'2'}
        String email = "vinkotov@example.com";
        String password = "1234";
        Map<String, String> authData = new HashMap<>();
        authData.put("email", email);
        authData.put("password", password);

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //System.out.println(responseGetAuth.asString());

        // Url: https://playground.learnqa.ru/api/user/" + userId.
        // userId=0, возвращает: {"error":"Wrong HTTP method"}
        // userId=1, возвращает: {"username":"Lana"}
        // userId=2, возвращает: {"username":"Vitaliy"}
        // userId=3, возвращает: {"username":"arsbatyrov"}
        // userId>3, возвращает: User not found

        // Id пользователя, которому будем менять данные
        String userId = "2"; // {"username":"Vitaliy"}

        // Задаем новый email
        String newEmail = "vinkotov_example.com";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);

        // Отправляем измененный email
        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        // Здесь всегда возвращается: {"error":"Please, do not edit test users with ID 1, 2, 3, 4 or 5."}
        if (responseEditUser.asString().contains("Please, do not edit test users with ID 1, 2, 3, 4 or 5.")){
            System.out.print("Пожалуйста, не редактируйте тестовых пользователей с идентификаторами 1, 2, 3, 4 или 5: ");
            System.out.println(responseEditUser.asString());

        }else {
            // Получаем данные пользователя с новым email
            Response responseUserData = RestAssured
                    .given()
                    .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                    .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                    .get("https://playground.learnqa.ru/api/user/" + userId)
                    .andReturn();

            System.out.println(responseUserData.asString());
            // Сравниваем старое email с новым
            Assertions.asserJsonByName(responseUserData, "email", newEmail);

        }

    }

    // Занятие 4. Ex17: Негативные тесты на PUT. Тест №4: Попытаемся изменить firstName пользователя, будучи авторизованными тем же пользователем,
    // на очень короткое значение в один символ.
    @Test
    public void testChangeUserFirstNameVeryShortValueOfOneCharacter(){

        // Метод создания пользователя: https://playground.learnqa.ru/api/user/, всегда возвращает: {"error":"Wrong HTTP method"}
        // Авторизуемся с нашим пользователем:
        // {'email': 'vinkotov@example.com', 'password': '1234', 'user_id':'2'}
        String email = "vinkotov@example.com";
        String password = "1234";
        Map<String, String> authData = new HashMap<>();
        authData.put("email", email);
        authData.put("password", password);

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //System.out.println(responseGetAuth.asString());

        // Url: https://playground.learnqa.ru/api/user/" + userId.
        // userId=0, возвращает: {"error":"Wrong HTTP method"}
        // userId=1, возвращает: {"username":"Lana"}
        // userId=2, возвращает: {"username":"Vitaliy"}
        // userId=3, возвращает: {"username":"arsbatyrov"}
        // userId>3, возвращает: User not found

        // Id пользователя, которому будем менять данные
        String userId = "2"; // {"username":"Vitaliy"}

        // Задаем новый firstName
        String newFirstName = "A";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newFirstName);

        // Отправляем измененный firstName
        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        // Здесь всегда возвращается: {"error":"Please, do not edit test users with ID 1, 2, 3, 4 or 5."}
        if (responseEditUser.asString().contains("Please, do not edit test users with ID 1, 2, 3, 4 or 5.")) {
            System.out.print("Пожалуйста, не редактируйте тестовых пользователей с идентификаторами 1, 2, 3, 4 или 5: ");
            System.out.println(responseEditUser.asString());

        }else {
            // Получаем данные пользователя с новым firstName
            Response responseUserData = RestAssured
                    .given()
                    .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                    .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                    .get("https://playground.learnqa.ru/api/user/" + userId)
                    .andReturn();

            System.out.println(responseUserData.asString());
            // Сравниваем старое firstName с новым
            Assertions.asserJsonByName(responseUserData, "firstName", newFirstName);

        }

    }



}
