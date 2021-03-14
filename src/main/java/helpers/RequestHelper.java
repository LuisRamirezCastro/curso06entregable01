package helpers;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class RequestHelper {

    public static String getUserToken(){
        Response response = given()
                .body(DataHelper.getPostsTestUser())
                .post("/v1/user/login");

        JsonPath jsonPathEvaluator = response.jsonPath();
        String token = jsonPathEvaluator.get("token.access_token");
        return token;
    }

    public static String getInvalidUserToken(){
        return "wrongtoken";
    }
}
