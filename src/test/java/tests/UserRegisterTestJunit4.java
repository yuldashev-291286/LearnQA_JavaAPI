package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTestJunit4 extends BaseTestCase {

    // Занятие 4. Учебный тест №1.
    @org.junit.Test
    public void testCreateUserWithExistingEmailOld(){
        String email = "vinkotov@example.com";

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
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");

    }

    // Занятие 4. Учебный тест №5.
    @org.junit.Test
    public void testCreateUserWithExistingEmail(){

        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");

    }

    // Занятие 4. Учебный тест №2.
    @org.junit.Test
    public void testCreateUserSuccessfullyOld(){
        String email = DataGenerator.getRandomEmail();

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

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        //System.out.println(responseCreateAuth.asString());
        Assertions.assertJsonHasField(responseCreateAuth, "id");

    }

    // Занятие 4. Учебный тест №6.
    @org.junit.Test
    public void testCreateUserSuccessfully(){
        String email = DataGenerator.getRandomEmail();

        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        //System.out.println(responseCreateAuth.asString());
        Assertions.assertJsonHasField(responseCreateAuth, "id");

    }


    // Занятие 4. Ex15: Тесты на метод user. ДЗ 1. Тест №1: Создание пользователя с некорректным email - без символа @.
    @org.junit.Test
    public void testCreateUserWithInvalidEmailWithoutTheATSymbol(){
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
    @Ignore
    @ParameterizedTest
    @CsvSource({
            "'', 1234, username, firstName, lastName",
            "vinkotov@example.com, '', username, firstName, lastName",
            "vinkotov@example.com, 1234, '', firstName, lastName",
            "vinkotov@example.com, 1234, username, '', lastName",
            "vinkotov@example.com, 1234, username, firstName, ''",
    })
    public void testCreateUserWithoutSpecifyingOneOfFields(String Email, String Password, String Username, String FirstName, String LastName){

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

        if (email == null | email == ""){
            Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: email");
        }else if (password == null | password == ""){
            Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: password");
        }else if (username == null | username == ""){
            Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: username");
        }else if (firstName == null | firstName == ""){
            Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: firstName");
        }else if (lastName == null | lastName == ""){
            Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: lastName");
        }


    }

    // Занятие 4. Ex15: Тесты на метод user. ДЗ 1. Тест №3: Создание пользователя с очень коротким именем в один символ.
    @org.junit.Test
    public void testCreateUserWithVeryShortOneCharacterName(){
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
    @org.junit.Test
    public void testCreateUserWithVeryLongNameLongerThan250Characters(){
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
