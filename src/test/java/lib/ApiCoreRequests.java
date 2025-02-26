package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {

    @Step("Get-запрос с token и cookie")
    public Response makeGetRequest(String url, String token, String cookie){

        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();


    }

    @Step("Get-запрос с cookie")
    public Response makeGetRequestWithCookies(String url, String cookie){

        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();


    }

    @Step("Get-запрос с token")
    public Response makeGetRequestWithToken(String url, String token){

        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();


    }

    @Step("Post-запрос")
    public Response makePostRequest(String url, Map<String, String> authData){

        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .get(url)
                .andReturn();


    }


}
