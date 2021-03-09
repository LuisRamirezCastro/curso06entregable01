import model.User;
import org.testng.annotations.Test;
import specifications.ResponseSpecs;

import static helpers.DataHelper.generateRandomEmail;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

public class UserTests extends BaseTest {

    private static String resourcePath = "/v1/user";

    @Test
    public void Test_Creat_User_Already_Exist(){

        User user = new User("Mauricio","pablo@test.com","castro");

        given()
                .body(user)
        .when()
                .post(String.format("%s/register",resourcePath))
        .then()
                .body("message", equalTo("User already exists"))
        .and()
                .statusCode(406)
                .spec(ResponseSpecs.defaultSpec());

    }

    @Test
    public void Test_Creat_User_Successful(){

        User user = new User("Mauricio", generateRandomEmail(),"castro");

        System.out.println("Test email" + user.getEmail());

        given()
                .body(user)
            .when()
                .post(String.format("%s/register",resourcePath))
            .then()
                .body("message", equalTo("Successfully registered"))
            .and()
                .statusCode(200)
                .spec(ResponseSpecs.defaultSpec());
    }
}
