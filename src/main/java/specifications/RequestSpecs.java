package specifications;

import helpers.DataHelper;
import helpers.RequestHelper;
import io.restassured.authentication.AuthenticationScheme;
import io.restassured.authentication.BasicAuthScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class RequestSpecs {

    // Valid authentication TOKEN generation
    public static RequestSpecification generateToken(){
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();

        String token = RequestHelper.getUserToken();

        requestSpecBuilder.addHeader("Authorization", "Bearer " + token);
        return requestSpecBuilder.build();
    };

    // Invalid authentication TOKEN generation
    public static RequestSpecification generateFakeToken(){
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();

        String wrongTokent = RequestHelper.getInvalidUserToken();

        requestSpecBuilder.addHeader("Authorization", "Bearer " + wrongTokent);
        return requestSpecBuilder.build();
    };


    // Valid authentication User/Pass generation
    public static RequestSpecification generateBasicAuthentication(){
        BasicAuthScheme schema = new BasicAuthScheme();
        schema.setUserName(DataHelper.getCommentsTestUser().getName());
        schema.setPassword(DataHelper.getCommentsTestUser().getPassword());

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setAuth(schema);
        return requestSpecBuilder.build();
    }

    // Invalid authentication User/Pass generation
    public static RequestSpecification generateInvalidBasicAuthentication(){
        BasicAuthScheme schema = new BasicAuthScheme();
        schema.setUserName(DataHelper.getInvalidCommentsTestUser().getName());
        schema.setPassword(DataHelper.getInvalidCommentsTestUser().getPassword());

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setAuth(schema);
        return requestSpecBuilder.build();
    }
}
