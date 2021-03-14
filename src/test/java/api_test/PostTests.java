package api_test;

import helpers.DataHelper;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
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

    // Triggers before the group
    @BeforeGroups("create_post")
    public static Integer createPost(){

        Post testPost = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        Response response = given()
                .spec(RequestSpecs.generateToken())
                .body(testPost)
                .post(resourcePath);

        JsonPath jsonPathEvaluator = response.jsonPath();
        createdPost = jsonPathEvaluator.get("id");

        return createdPost;
    }

    @BeforeGroups("manage_post")
    public static Integer createManagePost(){

        Post testPost = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        Response response = given()
                .spec(RequestSpecs.generateToken())
                .body(testPost)
                .post(resourcePath);

        JsonPath jsonPathEvaluator = response.jsonPath();
        createdPost = jsonPathEvaluator.get("id");

        return createdPost;
    }

    // - - - - - - TESTS for v1.POST("/post", TokenAuthMiddleware(), post.Create) - - - - - -
    // - - - - - - TESTS for v1.POST("/post", TokenAuthMiddleware(), post.Create) - - - - - -
    // - - - - - - TESTS for v1.POST("/post", TokenAuthMiddleware(), post.Create) - - - - - -
    @Test
    public void Test_Post_Create_Positive(){

        Post testPost = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        // Having a new post object, validates API response indicates creation was successful.
        given()
                .spec(RequestSpecs.generateToken())
                .body(testPost)
                .post(resourcePath)
                .then()
                .statusCode(200)
                .body("message", equalTo("Post created"))
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test
    public void Test_Post_Create_Negative(){

        // Having a no post object provided in request's body,
        // validates API response indicates an invalid form was provided.
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

        // Having a new post object with invalid authentication token,
        // validates API response indicates must login first.
        given()
                .spec(RequestSpecs.generateFakeToken())
                .body(testPost)
                .post(resourcePath)
                .then()
                .statusCode(401)
                .body("message", equalTo("Please login first"))
                .spec(ResponseSpecs.defaultSpec());
    }

    // - - - - - - TESTS for v1.GET("/posts", TokenAuthMiddleware(), post.All) - - - - - -
    // - - - - - - TESTS for v1.GET("/posts", TokenAuthMiddleware(), post.All) - - - - - -
    // - - - - - - TESTS for v1.GET("/posts", TokenAuthMiddleware(), post.All) - - - - - -
    @Test
    public void Test_Post_All_Positive(){

        // Validates API response indicates correct response.
        given()
                .spec(RequestSpecs.generateToken())
                //.body(testPost)
                .get(resourcePath+ "s")
                .then()
                .statusCode(200)
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test
    public void Test_Post_All_Schema_Positive(){

        // Validates JSON schema is correct for response
        Response response = given()
                .spec(RequestSpecs.generateToken())
                .get(resourcePath + "s");

        assertThat(response.asString(), matchesJsonSchemaInClasspath("posts.schema.json"));
    }
    @Test
    public void Test_Post_All_Negative(){

        // Validates API response indicates 404 if called POST instead of GET.
        given()
                .spec(RequestSpecs.generateToken())
                //.body(testPost)
                .post(resourcePath+ "s")
                .then()
                .statusCode(404)
                .body(containsString("Opss!! 404 again?"))
                .spec(ResponseSpecs.htmlSpec());
    }
    @Test
    public void Test_Post_All_Security(){

        // Validates API response indicates 404 if called POST instead of GET.
        given()
                .spec(RequestSpecs.generateFakeToken())
                .get(resourcePath+ "s")
                .then()
                .statusCode(401)
                .body("message", equalTo("Please login first"))
                .spec(ResponseSpecs.defaultSpec());
    }

    // - - - - - - TESTS for v1.GET("/post/:id", TokenAuthMiddleware(), post.One) - - - - - -
    // - - - - - - TESTS for v1.GET("/post/:id", TokenAuthMiddleware(), post.One) - - - - - -
    // - - - - - - TESTS for v1.GET("/post/:id", TokenAuthMiddleware(), post.One) - - - - - -
    @Test(groups = "manage_post")
    public void Test_Post_One_Positive(){

        // Having a new post previously done (ID stored in createdPost),
        // validates API response indicates get was successful.
        given()
                .spec(RequestSpecs.generateToken())
                .get(resourcePath + "/" + createdPost.toString())
                .then()
                .statusCode(200)
                .assertThat().body("data.id", equalTo(createdPost.intValue()) )
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "manage_post")
    public void Test_Post_One_Negative(){

        // Having a new post previously done (ID stored in createdPost),
        // validates API response indicates get did not succeeded for a non existent post.

        Integer nextPost = createdPost + 1;

//        System.out.println("CreatedPost: " + createdPost.toString());
//        System.out.println("NextPost: " + nextPost.toString());
//        System.out.println("URI: " + resourcePath + "/" + nextPost.toString());

        given()
                .spec(RequestSpecs.generateToken())
                .get(resourcePath + "/" + nextPost.toString())
                .then()
                .statusCode(404)
                .body("Message", equalTo("Post not found"))
                .body("error", equalTo("sql: no rows in result set"))
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "manage_post")
    public void Test_Post_One_Security(){

        // Having a new post previously done (ID stored in createdPost) and invalid authentication token,
        // validates API response indicates user must login before executing request.
        given()
                .spec(RequestSpecs.generateFakeToken())
                .get(resourcePath + "/" + createdPost.toString())
                .then()
                .statusCode(401)
                .body("message", equalTo("Please login first"))
                .spec(ResponseSpecs.defaultSpec());
    }
    // - - - - - - TESTS for v1.PUT("/post/:id", TokenAuthMiddleware(), post.Update) - - - - - -
    // - - - - - - TESTS for v1.PUT("/post/:id", TokenAuthMiddleware(), post.Update) - - - - - -
    // - - - - - - TESTS for v1.PUT("/post/:id", TokenAuthMiddleware(), post.Update) - - - - - -
    @Test(groups = "create_post")
    public void Test_Post_Update_Positive(){

        Post testPost = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        // Having an existing post object, validates API response indicates update was successful.
        given()
                .spec(RequestSpecs.generateToken())
                .body(testPost)
                .put(resourcePath + "/" + createdPost.toString())
                .then()
                .statusCode(200)
                .body("message", equalTo("Post updated"))
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "create_post")
    public void Test_Post_Update_Negative(){

        // Having a no post object provided in request's body,
        // validates API response indicates an invalid form was provided.
        given()
                .spec(RequestSpecs.generateToken())
                //.body(testPost)
                .put(resourcePath + "/" + createdPost.toString())
                .then()
                .statusCode(406)
                .body("message", equalTo("Invalid form"))
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "create_post")
    public void Test_Post_Update_Security(){

        Post testPost = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        // Having a new post object with invalid authentication token,
        // validates API response indicates must login first.
        given()
                .spec(RequestSpecs.generateFakeToken())
                .body(testPost)
                .put(resourcePath + "/" + createdPost.toString())
                .then()
                .statusCode(401)
                .body("message", equalTo("Please login first"))
                .spec(ResponseSpecs.defaultSpec());
    }

    // - - - - - - TESTS for v1.DELETE("/post/:id", TokenAuthMiddleware(), post.Delete) - - - - - -
    // - - - - - - TESTS for v1.DELETE("/post/:id", TokenAuthMiddleware(), post.Delete) - - - - - -
    // - - - - - - TESTS for v1.DELETE("/post/:id", TokenAuthMiddleware(), post.Delete) - - - - - -
    @Test(groups = "create_post")
    public void Test_Post_Delete_Positive(){

        // Having a new post previously done (ID stored in createdPost),
        // validates API response indicates delete was successful.

        given()
                .spec(RequestSpecs.generateToken())
                .delete(resourcePath + "/" + createdPost.toString())
                .then()
                .statusCode(200)
                .body("message", equalTo("Post deleted"))
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "create_post")
    public void Test_Post_Delete_Negative(){

        Integer nextPost = createdPost + 1;

        // Having a new post previously done (ID stored in createdPost),
        // validates API response indicates delete did not succeeded for a non existent post.
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

        // Having a new post previously done (ID stored in createdPost) and invalid authentication token,
        // validates API response indicates user must login before executing request.
        given()
                .spec(RequestSpecs.generateFakeToken())
                .delete(resourcePath + "/" + createdPost.toString())
                .then()
                .statusCode(401)
                .body("message", equalTo("Please login first"))
                .spec(ResponseSpecs.defaultSpec());
    }



    // - - - - - - TESTS for  - - - - - -
    // - - - - - - TESTS for  - - - - - -
    // - - - - - - TESTS for  - - - - - -

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

        assertThat(response.asString(), matchesJsonSchemaInClasspath("posts.schema.json"));
        //assertThat(response.path("results[0].data[0].id"),equalTo(802));
    }

}
