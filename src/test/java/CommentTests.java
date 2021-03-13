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
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;

public class CommentTests extends BaseTest{

    private static String resourcePath = "/v1/comment";

    private static Integer createdPost = 0;
    private static Integer createdComment = 0;

    public void createComment(int postId){

        Comment testComment = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        Response response = given()
                .spec(RequestSpecs.generateBasicAuthentication())
                .body(testComment)
                .post(resourcePath + "/" + postId);

        JsonPath jsonPathEvaluator = response.jsonPath();
        createdComment = jsonPathEvaluator.get("id");
        System.out.println("Comment id creado: "+ createdComment.toString());
    }

    @BeforeGroups("create_comment")
    public static Integer createPost(){

        createdPost = PostTests.createPost();
        //System.out.println("Post id creado: "+ createdPost.toString());

        return createdPost;
    }

    @BeforeGroups("get_all_comments")
    public static Integer createPostWithZeroComment(){

        createdPost = PostTests.createPost();
        System.out.println("Post id creado: "+ createdPost.toString());

        return createdPost;
    }

    @Test(groups = "create_comment")
    public void Test_Comment_Create_Positive(){
        System.out.println("\nTest_Comment_Create_Positive");

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
        System.out.println("\nTest_Comment_Create_Negative");

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
        System.out.println("\nTest_Comment_Create_Security");

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

    @Test(groups = "get_all_comments")
    public void Test_Comment_All_Zero_Comments_Positive(){
        System.out.println("\nTest_Comment_All_Zero_Comments_Positive - - - - CreatedPost: "+ createdPost.toString());

        given()
                .spec(RequestSpecs.generateBasicAuthentication())
                .get(resourcePath + "s/" + createdPost.toString())
                .then()
                .statusCode(200)
                .assertThat().body("results.meta.total[0]", equalTo(0) )
                .spec(ResponseSpecs.defaultSpec());

        //TODO Validate:
        /*
        {
            "results": [
                {
                    "data": [],
                    "meta": {
                        "total": 0
                    }
                }
            ]
        }
        */
    }
    @Test(groups = "get_all_comments")
    public void Test_Comment_All_Multiple_Comments_Positive(){
        System.out.println("\nTest_Comment_All_Multiple_Comments_Positive - - - - CreatedPost: "+ createdPost.toString());

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

        //TODO Validate:
        /*
        {
            "results": [
                {
                    "data": [],
                    "meta": {
                        "total": 0
                    }
                }
            ]
        }
        */
    }
    @Test(groups = "get_all_comments")
    public void Test_Comment_All_Negative(){
        System.out.println("\nTest_Comment_All_Negative - - - - CreatedPost: "+ createdPost.toString());

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
        System.out.println("\nTest_Comment_All_Security - - - - CreatedPost: "+ createdPost.toString());

        given()
                .spec(RequestSpecs.generateInvalidBasicAuthentication())
                .post(resourcePath + "/" + createdPost.toString())
                .then()
                .statusCode(401)
                .spec(ResponseSpecs.defaultSpec());
    }
}