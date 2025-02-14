import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;
import static java.nio.file.Files.lines;

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

    // Занятие №2. ДЗ №4. Ex8: Токены.
    @Test
    public void testRestAssured10() throws InterruptedException {

        String url = "https://playground.learnqa.ru/ajax/api/longtime_job";

        // 1. Создаем задачу.
        JsonPath response = RestAssured
                .get(url)
                .jsonPath();

        response.prettyPrint();

        String token = response.getString("token");
        int seconds = response.getInt("seconds");

        Map<String, String> param = new HashMap<>();
        param.put("token", token);

        // 2. Делаем один запрос с token ДО того, как задача готова, убеждаемся в правильности поля status.
        JsonPath response1 = RestAssured
                .given()
                .queryParams(param)
                .get(url)
                .jsonPath();

        response1.prettyPrint();

        String status = response1.getString("status");
        String error = response1.getString("error");

        if (status != null){
            if (response1.getString("status").equals("Job is NOT ready")){
                System.out.println("Задача еще не готова!");
            } else if (response1.getString("status").equals("Job is ready")) {
                System.out.println("Задача уже готова!");
            }
        }
        if (error != null){
            if (response1.getString("error").equals("No job linked to this token")){
                System.out.println("Передан token, для которого не создавалась задача!");
            }
        }

        // 3. Ждем нужное количество секунд с помощью функции Thread.sleep()
        int millis = (seconds*1000)+1000;
        sleep(millis);

        // 4. Делаем один запрос c token ПОСЛЕ того, как задача готова, убеждаемся в правильности поля status и наличии поля result.
        JsonPath response2 = RestAssured
                .given()
                .queryParams(param)
                .get(url)
                .jsonPath();

        response2.prettyPrint();

        String result = response2.getString("result");
        String newStatus = response2.getString("status");

        if (result != null){
            if (newStatus.equals("Job is ready")){
                System.out.println("Задача готова!");
            }
        }

    }

    // Занятие №2. ДЗ №5. Ex9: Подбор пароля.
    @Test
    public void testRestAssured11(){

        // Читаем пароли из файла
        // Сохраняем пароли в коллекцию List
        URI uri = null;
        try {
            uri = ClassLoader.getSystemResource("Passwords.txt").toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        List<String> listOfPasswords = null;
        try (Stream<String> lines = Files.lines(Paths.get(uri))) {
            listOfPasswords = lines.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //listOfPasswords.forEach(System.out::println);
        //System.out.println(listOfPasswords.size());

        String getSecretPasswordHomework = "https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
        String checkAuthCookie = "https://playground.learnqa.ru/ajax/api/check_auth_cookie";

        String login = "super_admin";

        for (int i = 0; i < listOfPasswords.size(); i++) {

            // 1. Берем очередной пароль и вместе с логином коллеги вызываем первый метод get_secret_password_homework.
            // В ответ метод будет возвращать авторизационную cookie с именем auth_cookie и каким-то значением.

            String password = listOfPasswords.get(i);

            Map<String, String> data1 = new HashMap<>();
            data1.put("login", login);
            data1.put("password", password);

            JsonPath response1 = RestAssured
                    .given()
                    .body(data1)
                    .when()
                    .post(getSecretPasswordHomework)
                    .jsonPath();

            //response1.prettyPrint();

            String cookiePassword = response1.getString("password");
            String cookieEquals = response1.getString("equals");

            //System.out.println(cookiePassword);
            //System.out.println(cookieEquals);

            // 2. Далее эту cookie мы передаем во второй метод check_auth_cookie.

            Map<String, String> data2 = new HashMap<>();
            data2.put("password", cookiePassword);
            data2.put("equals", cookieEquals);

            Response response2 = RestAssured
                    .given()
                    .body(data2)
                    .when()
                    .post(checkAuthCookie)
                    .andReturn();

            //response2.print();

            // Если в ответ вернулась фраза "You are NOT authorized", значит пароль неправильный.
            // В этом случае берем следующий пароль и все заново.
            // Если же вернулась другая фраза - нужно, чтобы программа вывела верный пароль и эту фразу.

            String phraseInResponse = response2.toString();

            if (phraseInResponse.equals("You are NOT authorized") == true){
                continue;
            }else if (phraseInResponse.equals("You are NOT authorized") == false) {
                System.out.println("Password: " + cookiePassword);
                System.out.println("You are authorized");
                break;
            }

        }

    }


}
