package specifications;

import helpers.RequestHelper;
import io.restassured.authentication.AuthenticationScheme;
import io.restassured.authentication.BasicAuthScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class RequestSpecs {

    public static RequestSpecification generateToken(){
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();

        String token = RequestHelper.getUserToken();

        requestSpecBuilder.addHeader("Authorization", "Bearer " + token);
        return requestSpecBuilder.build();
    };

    public static RequestSpecification generateFakeToken(){
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.addHeader("Authorization", "Beasadrer wrongtoken");
        return requestSpecBuilder.build();
    };

    public static RequestSpecification generateBasicAuthentication(){
        BasicAuthScheme schema = new BasicAuthScheme();
        schema.setUserName("testuser");
        schema.setPassword("testpass");

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setAuth(schema);
        return requestSpecBuilder.build();
    }

    public static RequestSpecification generateInvalidBasicAuthentication(){
        BasicAuthScheme schema = new BasicAuthScheme();
        schema.setUserName("testuserinvalid");
        schema.setPassword("testpassinvalid");

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setAuth(schema);
        return requestSpecBuilder.build();
    }
}
