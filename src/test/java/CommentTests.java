import helpers.DataHelper;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import model.Comment;
import model.Post;
import org.hamcrest.core.Is;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import specifications.RequestSpecs;
import specifications.ResponseSpecs;

import java.sql.Array;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;

public class CommentTests extends BaseTest{

    private static String resourcePath = "/v1/comment";
    private static Integer createdPost = 0;
    private static Integer createdComment = 0;

    public static void createComment(int postId){

        Comment testComment = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        Response response = given()
                .spec(RequestSpecs.generateBasicAuthentication())
                .body(testComment)
                .post(resourcePath + "/" + postId);

        JsonPath jsonPathEvaluator = response.jsonPath();
        createdComment = jsonPathEvaluator.get("id");
    }

    @BeforeGroups("create_comment")
    public static Integer createPost(){

        createdPost = PostTests.createPost();
        return createdPost;
    }

    @BeforeGroups("manage_comment")
    public static Integer createPostWithComment(){

        createdPost = PostTests.createPost();
        createComment(createdPost.intValue());
        return createdComment;
    }

    @BeforeGroups("get_all_comments")
    public static Integer createPostWithZeroComment(){

        createdPost = PostTests.createPost();
        return createdPost;
    }

    // - - - - - - TESTS for v1.POST("/comment/:postid", basicAuth(), comment.Create) - - - - - -
    // - - - - - - TESTS for v1.POST("/comment/:postid", basicAuth(), comment.Create) - - - - - -
    // - - - - - - TESTS for v1.POST("/comment/:postid", basicAuth(), comment.Create) - - - - - -
    @Test(groups = "create_comment")
    public void Test_Comment_Create_Positive(){

        Comment testComment = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given()
                .spec(RequestSpecs.generateBasicAuthentication())
                .body(testComment)
                .post(resourcePath + "/" + createdPost.toString())
                .then()
                .statusCode(200)
                .body("message", equalTo("Comment created"))
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "create_comment")
    public void Test_Comment_Create_Negative(){

        Comment testComment = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given()
                .spec(RequestSpecs.generateBasicAuthentication())
                .post(resourcePath + "/" + createdPost.toString())
                .then()
                .statusCode(406)
                .body("message", equalTo("Invalid form"))
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "create_comment")
    public void Test_Comment_Create_Security(){

        Comment testComment = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given()
                .spec(RequestSpecs.generateInvalidBasicAuthentication())
                .body(testComment)
                .post(resourcePath + "/" + createdPost.toString())
                .then()
                .statusCode(401)
                .body("message", equalTo("Please login first"))
                .spec(ResponseSpecs.defaultSpec());
    }

    // - - - - - - TESTS for v1.GET("/comments/:postid", basicAuth(), comment.All) - - - - - -
    // - - - - - - TESTS for v1.GET("/comments/:postid", basicAuth(), comment.All) - - - - - -
    // - - - - - - TESTS for v1.GET("/comments/:postid", basicAuth(), comment.All) - - - - - -
    @Test(groups = "get_all_comments")
    public void Test_Comment_All_Zero_Comments_Positive(){

        given()
                .spec(RequestSpecs.generateBasicAuthentication())
                .get(resourcePath + "s/" + createdPost.toString())
                .then()
                .statusCode(200)
                .assertThat().body("results.meta.total[0]", equalTo(0) )
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "get_all_comments")
    public void Test_Comment_All_Multiple_Comments_Positive(){

        // Creates 2 comments on existing post
        createComment(createdPost.intValue());
        createComment(createdPost.intValue());

        given()
                .spec(RequestSpecs.generateBasicAuthentication())
                .get(resourcePath + "s/" + createdPost.toString())
                .then()
                .statusCode(200)
                .assertThat().body("results.meta.total[0]", equalTo(2) )
                .spec(ResponseSpecs.defaultSpec());


    }
    @Test(groups = "get_all_comments")
    public void Test_Comment_All_Schema_Positive(){

        // Validates JSON schema is correct for response
        Response response = given()
                .spec(RequestSpecs.generateBasicAuthentication())
                .get(resourcePath + "s/" + createdPost.toString());

        assertThat(response.asString(), matchesJsonSchemaInClasspath("comments.schema.json"));
    }
    @Test(groups = "get_all_comments")
    public void Test_Comment_All_Negative(){

        given()
                .spec(RequestSpecs.generateBasicAuthentication())
                .post(resourcePath + "s/" + createdPost.toString())
                .then()
                .statusCode(404)
                .body(containsString("Opss!! 404 again?"))
                .spec(ResponseSpecs.htmlSpec());
    }
    @Test(groups = "get_all_comments")
    public void Test_Comment_All_Security(){

        given()
                .spec(RequestSpecs.generateInvalidBasicAuthentication())
                .get(resourcePath + "s/" + createdPost.toString())
                .then()
                .statusCode(401)
                .body("message", equalTo("Please login first"))
                .spec(ResponseSpecs.defaultSpec());
    }

    // - - - - - - TESTS for v1.GET("/comment/:postid/:id", basicAuth(), comment.One) - - - - - -
    // - - - - - - TESTS for v1.GET("/comment/:postid/:id", basicAuth(), comment.One) - - - - - -
    // - - - - - - TESTS for v1.GET("/comment/:postid/:id", basicAuth(), comment.One) - - - - - -
    @Test(groups = "manage_comment")
    public void Test_Comment_One_Positive(){

        given()
                .spec(RequestSpecs.generateBasicAuthentication())
                .get(resourcePath + "/" + createdPost.toString() + "/" + createdComment.toString())
                .then()
                .statusCode(200)
                .assertThat().body("data.id", equalTo(createdComment.intValue()))
                .assertThat().body("data.post_id", equalTo(createdPost.toString()))
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "manage_comment")
    public void Test_Comment_One_Negative(){

        Integer nextComment = createdComment + 1;
        given()
                .spec(RequestSpecs.generateBasicAuthentication())
                .get(resourcePath + "/" + createdPost.toString() + "/" + nextComment.toString())
                .then()
                .statusCode(404)
                .body("Message", equalTo("Comment not found"))
                .body("error", equalTo("sql: no rows in result set"))
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "manage_comment")
    public void Test_Comment_One_Security(){

        given()
                .spec(RequestSpecs.generateInvalidBasicAuthentication())
                //.body(testComment)
                .get(resourcePath + "/" + createdPost.toString() + "/" + createdComment.toString())
                .then()
                .statusCode(401)
                .body("message", equalTo("Please login first"))
                .spec(ResponseSpecs.defaultSpec());
    }

    // - - - - - - TESTS for v1.PUT("/comment/:postid/:id", basicAuth(), comment.Update) - - - - - -
    // - - - - - - TESTS for v1.PUT("/comment/:postid/:id", basicAuth(), comment.Update) - - - - - -
    // - - - - - - TESTS for v1.PUT("/comment/:postid/:id", basicAuth(), comment.Update) - - - - - -
    @Test(groups = "manage_comment")
    public void Test_Comment_Update_Positive(){

        Comment updatedComment = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given()
                .spec(RequestSpecs.generateBasicAuthentication())
                .body(updatedComment)
                .put(resourcePath + "/" + createdPost.toString() + "/" + createdComment.toString())
                .then()
                .statusCode(200)
                .body("message", equalTo("Comment updated"))
                //.assertThat().body("data.id", equalTo(createdComment.intValue()))
                //.assertThat().body("data.post_id", equalTo(createdPost.toString()))
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "manage_comment")
    public void Test_Comment_Update_Negative(){

        Comment updatedComment = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        Integer nextComment = createdComment + 1;
        given()
                .spec(RequestSpecs.generateBasicAuthentication())
                .body(updatedComment)
                .put(resourcePath + "/" + createdPost.toString() + "/" + nextComment.toString())
                .then()
                .statusCode(406)
                .body("message", equalTo("Comment could not be updated"))
                .body("error", equalTo("Comment not found"))
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "manage_comment")
    public void Test_Comment_Uodate_Security(){

        Comment updatedComment = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given()
                .spec(RequestSpecs.generateInvalidBasicAuthentication())
                .body(updatedComment)
                .put(resourcePath + "/" + createdPost.toString() + "/" + createdComment.toString())
                .then()
                .statusCode(401)
                .body("message", equalTo("Please login first"))
                .spec(ResponseSpecs.defaultSpec());
    }
    // - - - - - - TESTS for v1.DELETE("/comment/:postid/:id", basicAuth(), comment.Delete) - - - - - -
    // - - - - - - TESTS for v1.DELETE("/comment/:postid/:id", basicAuth(), comment.Delete) - - - - - -
    // - - - - - - TESTS for v1.DELETE("/comment/:postid/:id", basicAuth(), comment.Delete) - - - - - -
    @Test(groups = "manage_comment")
    public void Test_Comment_Delete_Positive(){

        given()
                .spec(RequestSpecs.generateBasicAuthentication())
                .delete(resourcePath + "/" + createdPost.toString() + "/" + createdComment.toString())
                .then()
                .statusCode(200)
                .body("message", equalTo("Comment deleted"))
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "manage_comment")
    public void Test_Comment_Delete_Negative(){

        Integer nextComment = createdComment + 1;
        given()
                .spec(RequestSpecs.generateBasicAuthentication())
                .delete(resourcePath + "/" + createdPost.toString() + "/" + nextComment.toString())
                .then()
                .statusCode(406)
                .body("message", equalTo("Comment could not be deleted"))
                .body("error", equalTo("Comment not found"))
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "manage_comment")
    public void Test_Comment_Delete_Security(){

        given()
                .spec(RequestSpecs.generateInvalidBasicAuthentication())
                //.body(testComment)
                .delete(resourcePath + "/" + createdPost.toString() + "/" + createdComment.toString())
                .then()
                .statusCode(401)
                .body("message", equalTo("Please login first"))
                .spec(ResponseSpecs.defaultSpec());
    }



}