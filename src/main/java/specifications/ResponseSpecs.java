package specifications;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

public class ResponseSpecs {

    public static ResponseSpecification defaultSpec() {
        ResponseSpecBuilder responseBuilder = new ResponseSpecBuilder();
        responseBuilder.expectHeader("Content-Type", "application/json; charset=utf-8");
        responseBuilder.expectHeader("Access-Control-Allow-Origin", "http://localhost");

        return responseBuilder.build();
    }
}
