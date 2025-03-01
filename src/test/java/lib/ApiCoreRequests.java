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

public class ApiCoreRequests extends BaseTestCase {

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

    // Занятие 4. Ex16: Запрос данных другого пользователя.
    // ДЗ 2. В этой задаче нужно написать тест, который делает авторизацию одним пользователем, но получает данные другого (т.е. с другим ID).
    // И убедиться, что в этом случае запрос также получает только username, так как мы не должны видеть остальные данные чужого пользователя.
    @Step("Тест №5: Тест, который делает авторизацию одним пользователем, но получает данные другого, т.е. с другим ID.")
    @Test
    public void logsInAsOneUserButReceivesDataFromAnother(){

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/3")
                .andReturn();

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");

    }

    // Занятие 4. ДЗ 3. Ex17: Негативные тесты на PUT. Тест №1: Попытаемся изменить данные пользователя, будучи неавторизованными.
    @Step("Тест №6: Тест, который попытается изменить данные пользователя, будучи неавторизованными.")
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

    // Занятие 4. ДЗ 3. Ex17: Негативные тесты на PUT. Тест №2: Попытаемся изменить данные пользователя, будучи авторизованными другим пользователем.
    @Step("Тест №7: Тест, который пытается изменить данные пользователя, будучи авторизованными другим пользователем.")
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

    // Занятие 4. ДЗ 3. Ex17: Негативные тесты на PUT. Тест №3: Попытаемся изменить email пользователя, будучи авторизованными тем же пользователем, на новый email без символа @.
    @Step("Тест №8: Тест, который пытается изменить email пользователя, будучи авторизованными тем же пользователем, на новый email без символа @.")
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

    // Занятие 4. ДЗ 3. Ex17: Негативные тесты на PUT. Тест №4: Попытаемся изменить firstName пользователя, будучи авторизованными тем же пользователем,
    // на очень короткое значение в один символ.
    @Step("Тест №9: Тест, который пытается изменить firstName пользователя, будучи авторизованным тем же пользователем на очень короткое значение в один символ.")
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
