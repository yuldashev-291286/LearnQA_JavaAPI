import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HelloWorldTest {

    // Занятие №1, ДЗ №3
    @Test
    public void testMyName(){

        System.out.println("Hello from Ruslan");
    }

    // Занятие №1, ДЗ №4
    // Тест, который отправляет GET-запрос по адресу: https://playground.learnqa.ru/api/get_text
    // Выведено содержимое текста в ответе на запрос
    @Test
    public void testSendGETRequestToAddress(){

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }


    // Занятие №2, Учебный тест №1
    @Test
    public void testRestAssured1(){

        Map<String, String> params = new HashMap<>();
        params.put("name", "John");

        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("http://playground.learnqa.ru/api/hello")
                .jsonPath();

        String name = response.get("answer2");
        if (name == null){
            System.out.println("The key 'answer2' is absent");
        } else {
            System.out.println(name);
        }
    }

    // Занятие №2, Учебный тест №2
    @Test
    public void testRestAssured2(){

        Map<String,Object> body = new HashMap<>();
        body.put("param1","value1");
        body.put("param2","value2");

        Response response = RestAssured
                .given()
                .body(body)
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        response.print();
    }

    // Занятие №2, Учебный тест №3
    @Test
    public void testRestAssured3(){

        Response response = RestAssured
                .given()
                .redirects()
                .follow(true)
                .when()
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();

        int statusCode = response.getStatusCode();
        System.out.println(statusCode);
    }

    // Занятие №2, Учебный тест №4
    @Test
    public void testRestAssured4(){

        Map<String,String> headers = new HashMap<>();
        headers.put("MyHeaders1", "MyValues1");
        headers.put("MyHeaders2", "MyValues2");

        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();

        response.prettyPrint();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
    }

    // Занятие №2, Учебный тест №5
    @Test
    public void testRestAssured5(){

        Map<String,String> data = new HashMap<>();
        data.put("login", "secret_login2");
        data.put("password", "secret_pass2");

        Response response = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        System.out.println("\nPretty text:");
        response.prettyPrint();

        System.out.println("\nHeaders:");
        Headers responseHeaders = response.getHeaders();
        System.out.println(responseHeaders);

        System.out.println("\nCookies:");
        Map<String,String> responseCookies = response.getCookies();
        System.out.println(responseCookies);

        System.out.println("\nCookies:");
        String responseCookie = response.getCookie("auth_cookie");
        System.out.println(responseCookie);

    }

    // Занятие №2, Учебный тест №6
    @Test
    public void testRestAssured6(){

        Map<String,String> data = new HashMap<>();
        data.put("login", "secret_login");
        data.put("password", "secret_pass");

        Response responseForGet = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        String responseCookie = responseForGet.getCookie("auth_cookie");

        Map<String,String> cookies = new HashMap<>();
        if (responseCookie != null){
            cookies.put("auth_cookie", responseCookie);
        }

        Response responseForCheck = RestAssured
                .given()
                .body(data)
                .cookie(cookies.toString())
                .when()
                .post("https://playground.learnqa.ru/api/check_auth_cookie")
                .andReturn();

        responseForCheck.print();

    }

    // Занятие №2. ДЗ №1. Ex5: Парсинг JSON.
    @Test
    public void testRestAssured7(){

        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        response.prettyPrint();

        String textOfTheSecondMessage = response.getString("messages[1].message");
        System.out.println(textOfTheSecondMessage);

    }

    // Занятие №2. ДЗ №2. Ex6: Редирект.
    @Test
    public void testRestAssured8(){

        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        response.prettyPrint();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);

    }

    // Занятие №2. ДЗ №3. Ex7: Долгий редирект.
    @Test
    public void testRestAssured9(){

        String url = "https://playground.learnqa.ru/api/long_redirect";
        Response response = RestAssured
                .given()
                .redirects()
                .follow(true)
                .when()
                .get(url)
                .andReturn();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
        int statusCode = response.getStatusCode();
        System.out.println(statusCode);

        while (statusCode != 200){

            Response nextResponse = RestAssured
                    .given()
                    .redirects()
                    .follow(true)
                    .when()
                    .get(url)
                    .andReturn();

            String nextLocationHeader = nextResponse.getHeader("Location");
            System.out.println(nextLocationHeader);
            int nextStatusCode = nextResponse.getStatusCode();
            System.out.println(nextStatusCode);
            statusCode = nextStatusCode;

        }


    }


}
