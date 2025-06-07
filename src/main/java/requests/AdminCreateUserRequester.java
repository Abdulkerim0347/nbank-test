package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.CreateUserRequest;

import static io.restassured.RestAssured.given;

public class AdminCreateUserRequester extends Request<CreateUserRequest> {
    public AdminCreateUserRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(CreateUserRequest model) {
        return given()
                .spec(requestSpecification)
                .body(model)
                .post("api/v1/admin/users")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse put(CreateUserRequest model) {
        return null;
    }

    @Override
    public ValidatableResponse get(CreateUserRequest model) {
        return null;
    }

    @Override
    public ValidatableResponse delete(CreateUserRequest model) {
        return null;
    }
}
