package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTestJunit4 extends BaseTestCase{

    // Занятие 4. Ex18: Тесты на DELETE. Тест №1: Попытка удалить пользователя по ID 2. Убедиться, что система не даст вам удалить этого пользователя.
    @org.junit.Test
    public void testAttemptDeleteAuthorizedUserByIDTwo(){

        // Метод создания пользователя: https://playground.learnqa.ru/api/user/, всегда возвращает: {"error":"Wrong HTTP method"}
        // Генерировать нового пользователя мы не можем.
        // responseCreateAuth.getString("id") возвращает ошибку.

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

        //responseGetAuth.prettyPrint();

        // Id пользователя, которого будем пытаться удалить
        String userId = "2"; // {"username":"Vitaliy"}

        // Удаляем пользователя по ID
        Response responseDeleteUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .delete(" https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //responseDeleteUser.prettyPrint();

        // Здесь всегда возвращается: {"error":"Please, do not delete test users with ID 1, 2, 3, 4 or 5."}
        if (responseDeleteUser.asString().contains("Please, do not delete test users with ID 1, 2, 3, 4 or 5.")){
            System.out.println("Пожалуйста, не удаляйте тестовых пользователей с идентификаторами 1, 2, 3, 4 или 5: ");
            responseDeleteUser.print();

        }


    }

    // Занятие 4. Ex18: Тесты на DELETE. Тест №2: Создать пользователя, авторизоваться из-под него, удалить,
    // затем попробовать получить его данные по ID и убедиться, что пользователь действительно удален.
    // ! Тест не работает, так как не работает метод создания нового пользователя.
    @org.junit.Test
    public void testCreateUserLoginDeleteGetHisDataByID(){

        // Генерация нового пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        // Создания нового пользователя
        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        // Метод создания пользователя: https://playground.learnqa.ru/api/user/, всегда возвращает: {"error":"Wrong HTTP method"}
        // Генерировать нового пользователя мы не можем.
        // responseCreateAuth.getString("id") возвращает ошибку.

        // Метод изменения пользователя: https://playground.learnqa.ru/api/user/" + userId.
        // userId=0, возвращает: {"error":"Wrong HTTP method"}
        // userId=1, возвращает: {"username":"Lana"}
        // userId=2, возвращает: {"username":"Vitaliy"}
        // userId=3, возвращает: {"username":"arsbatyrov"}
        // userId>3, возвращает: User not found

        // Id пользователя, которого будем удалять
        // responseCreateAuth.getString("id") возвращает ошибку.
        String userId = responseCreateAuth.getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        // Авторизация по email и password
        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //responseGetAuth.print();

        // Удаляем пользователя по ID
        Response responseDeleteUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .delete(" https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        // Получаем данные удаленного пользователя
        JsonPath responseDeleteUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .jsonPath();

        //responseDeleteUserData.prettyPrint();

        // Убеждаемся, что пользователь действительно удален
        if (responseDeleteUserData.getString("id").equals(null) == true){
            System.out.println("Пользователь удален !");

        }else {
            System.out.println("Пользователь не удален.");

        }

    }

    // Занятие 4. Ex18: Тесты на DELETE. Тест №3: Попробовать удалить пользователя, будучи авторизованным другим пользователем.
    @org.junit.Test
    public void testDeleteUserWhileLoggedByAnotherUser(){

        // Метод создания пользователя: https://playground.learnqa.ru/api/user/, всегда возвращает: {"error":"Wrong HTTP method"}
        // Генерировать нового пользователя мы не можем.
        // responseCreateAuth.getString("id") возвращает ошибку.

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

        //responseGetAuth.prettyPrint();

        if (responseGetAuth.jsonPath().getString("user_id").equals("2") == true){
            System.out.println("Наш авторизованный пользователь: Vinkotov Vitaliy.");
        }else {
            System.out.println("Это не наш авторизованный пользователь !");
        }

        // Метод изменения пользователя: https://playground.learnqa.ru/api/user/" + userId.
        // userId=0, возвращает: {"error":"Wrong HTTP method"}
        // userId=1, возвращает: {"username":"Lana"}
        // userId=2, возвращает: {"username":"Vitaliy"}
        // userId=3, возвращает: {"username":"arsbatyrov"}
        // userId>3, возвращает: User not found

        // Id пользователя, которого будем пытаться удалить
        String userId = "1"; // {"username":"Lana"}
        System.out.println("Пытаемся удалить пользователя c userId=" + userId + " и username=Lana");

        // Удаляем пользователя по ID
        Response responseDeleteUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .delete(" https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //responseDeleteUser.prettyPrint();

        // Здесь всегда возвращается: {"error":"Please, do not delete test users with ID 1, 2, 3, 4 or 5."}
        if (responseDeleteUser.asString().contains("Please, do not delete test users with ID 1, 2, 3, 4 or 5.")){
            System.out.println("Пожалуйста, не удаляйте тестовых пользователей с идентификаторами 1, 2, 3, 4 или 5: ");
            responseDeleteUser.print();

        }

    }



}
