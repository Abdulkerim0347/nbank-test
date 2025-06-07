package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.LoginUserRequest;

import static io.restassured.RestAssured.given;

public class LoginUserRequester extends Request<LoginUserRequest> {
    public LoginUserRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(LoginUserRequest model) {
        return given()
                .spec(requestSpecification)
                .body(model)
                .post("/api/v1/auth/login")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse put(LoginUserRequest model) {
        return null;
    }

    @Override
    public ValidatableResponse get(LoginUserRequest model) {
        return null;
    }

    @Override
    public ValidatableResponse delete(LoginUserRequest model) {
        return null;
    }
}
