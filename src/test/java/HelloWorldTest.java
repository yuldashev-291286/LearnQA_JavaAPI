import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.jupiter.api.Test;

public class HelloWorldTest {

    @Test
    public void testHelloWorld(){

        Response response = RestAssured
                .get("http://playground.learnqa.ru/api/hello")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    public void testMyName(){

        System.out.println("Hello from Ruslan");
    }

    // Тест, который отправляет GET-запрос по адресу: https://playground.learnqa.ru/api/get_text
    // Выведено содержимое текста в ответе на запрос
    @Test
    public void testSendGETRequestToAddress(){

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }

}
