import helpers.DataHelper;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import model.Article;
import model.Post;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import specifications.RequestSpecs;
import specifications.ResponseSpecs;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;

public class PostTests extends BaseTest{

    private static String resourcePath = "/v1/post";

    private static Integer createdPost = 0;

    @BeforeGroups("create_post")
    public void createPost(){

        Post testPost = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        Response response = given()
                .spec(RequestSpecs.generateToken())
                .body(testPost)
                .post(resourcePath);

        JsonPath jsonPathEvaluator = response.jsonPath();
        createdPost = jsonPathEvaluator.get("id");
        System.out.println("Post id creado: "+ createdPost.toString());

    }

    // - - - - - - TESTS - - - - - -
    // - - - - - - TESTS - - - - - -
    // - - - - - - TESTS - - - - - -

    @Test
    public void Test_Post_Create_Positive(){

        Post testPost = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        given()
                .spec(RequestSpecs.generateToken())
                .body(testPost)
                .post(resourcePath)
                .then()
                .statusCode(200)
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test
    public void Test_Post_Create_Negative(){

        Post testPost = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        given()
                .spec(RequestSpecs.generateToken())
                //.body(testPost)
                .post(resourcePath)
                .then()
                .statusCode(406)
                .body("message", equalTo("Invalid form"))
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test
    public void Test_Post_Create_Security(){

        Post testPost = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        given()
                .spec(RequestSpecs.generateFakeToken())
                //.body(testPost)
                .post(resourcePath)
                .then()
                .statusCode(401)
                .body("message", equalTo("Please login first"))
                .spec(ResponseSpecs.defaultSpec());
    }

    @Test(groups = "create_post")
    public void Test_Post_Delete_Positive(){

        given()
                .spec(RequestSpecs.generateToken())
                .delete(resourcePath + "/" + createdPost.toString())
                .then()
                .statusCode(200)
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "create_post")
    public void Test_Post_Delete_Negative(){

        Integer nextPost = createdPost + 1;

        given()
                .spec(RequestSpecs.generateToken())
                .delete(resourcePath + "/" + nextPost.toString())
                .then()
                .statusCode(406)
                .body("error", equalTo("Post not found"))
                .body("message", equalTo("Post could not be deleted"))
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "create_post")
    public void Test_Post_Delete_Security(){

        given()
                .spec(RequestSpecs.generateFakeToken())
                .delete(resourcePath + "/" + createdPost.toString())
                .then()
                .statusCode(401)
                .body("message", equalTo("Please login first"))
                .spec(ResponseSpecs.defaultSpec());
    }


    //@Test
    public void Test_Invalid_Token_Cant_Create_New_Posts(){

        Post testPost = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        given()
                .spec(RequestSpecs.generateFakeToken())
                .body(testPost)
                .post(resourcePath)
                .then()
                .statusCode(401)
                .body("message", equalTo("Please login first"))
                .spec(ResponseSpecs.defaultSpec());
    }

    //@Test(groups = "create_article")
    public void Test_Articles_Schema(){
        Response response = given()
                .spec(RequestSpecs.generateToken())
                .get(resourcePath + "s");

        assertThat(response.asString(), matchesJsonSchemaInClasspath("articles.schema.json"));
        assertThat(response.path("results[0].data[0].id"),equalTo(802));
    }

}
