package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {

    @Step("Get-запрос с token и cookie")
    public Response makeGetRequest(String url, String token, String cookie) {

        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();


    }

    @Step("Get-запрос с cookie")
    public Response makeGetRequestWithCookies(String url, String cookie) {

        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();


    }

    @Step("Get-запрос с token")
    public Response makeGetRequestWithToken(String url, String token) {

        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();


    }

    @Step("Post-запрос")
    public Response makePostRequest(String url, Map<String, String> authData) {

        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .get(url)
                .andReturn();


    }

    // Занятие 4. Ex15: Тесты на метод user. ДЗ 1. Тест №1: Создание пользователя с некорректным email - без символа @.
    @Step("Тест №1: Создание пользователя с некорректным email - без символа @.")
    @Test
    public void testCreateUserWithInvalidEmailWithoutTheATSymbol() {
        String email = "vinkotov_example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", "123");
        userData.put("username", "learnqa");
        userData.put("firstName", "learnqa");
        userData.put("lastName", "learnqa");

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");

    }

    // Занятие 4. Ex15: Тесты на метод user. ДЗ 1. Тест №2: Создание пользователя без указания одного из полей. С помощью @ParameterizedTest необходимо проверить,
    // что отсутствие любого параметра не дает зарегистрировать пользователя.
    // Параметры: vinkotov@example.com, 1234, username, firstName, lastName
    @Step("Тест №2: Создание пользователя без указания одного из полей. С помощью @ParameterizedTest необходимо проверить, что отсутствие любого параметра не дает зарегистрировать пользователя.")
    @ParameterizedTest
    @CsvSource({
            "'', 1234, username, firstName, lastName",
            "vinkotov@example.com, '', username, firstName, lastName",
            "vinkotov@example.com, 1234, '', firstName, lastName",
            "vinkotov@example.com, 1234, username, '', lastName",
            "vinkotov@example.com, 1234, username, firstName, ''",
    })
    public void testCreateUserWithoutSpecifyingOneOfFields(String Email, String Password, String Username, String FirstName, String LastName) {

        String email;
        String password;
        String username;
        String firstName;
        String lastName;

        email = Email;
        password = Password;
        username = Username;
        firstName = FirstName;
        lastName = LastName;

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", password);
        userData.put("username", username);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        if (email == null | email == "") {
            Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: email");
        } else if (password == null | password == "") {
            Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: password");
        } else if (username == null | username == "") {
            Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: username");
        } else if (firstName == null | firstName == "") {
            Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: firstName");
        } else if (lastName == null | lastName == "") {
            Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: lastName");
        }


    }

    // Занятие 4. Ex15: Тесты на метод user. ДЗ 1. Тест №3: Создание пользователя с очень коротким именем в один символ.
    @Step("Тест №3: Создание пользователя с очень коротким именем в один символ.")
    @Test
    public void testCreateUserWithVeryShortOneCharacterName() {
        String shortUsername = "W";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", "vinkotov@example.com");
        userData.put("password", "123");
        userData.put("username", shortUsername);
        userData.put("firstName", "learnqa");
        userData.put("lastName", "learnqa");

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too short");

    }

    // Занятие 4. Ex15: Тесты на метод user. ДЗ 1. Тест №4: Создание пользователя с очень длинным именем - длиннее 250 символов.
    @Step("Тест №4: Создание пользователя с очень длинным именем - длиннее 250 символов.")
    @Test
    public void testCreateUserWithVeryLongNameLongerThan250Characters() {
        String longUsername = "12345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", "vinkotov@example.com");
        userData.put("password", "123");
        userData.put("username", longUsername);
        userData.put("firstName", "learnqa");
        userData.put("lastName", "learnqa");

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too long");

    }


}
