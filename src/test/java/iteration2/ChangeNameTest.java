package iteration2;

import generators.RandomData;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import iteration1.BaseTest;
import models.*;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
import requests.ChangeNameRequester;
import requests.LoginUserRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

public class ChangeNameTest extends BaseTest {

    @Test
    public void userCanChangeItsNameTest() {
        var userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        var request = ChangeNameRequest.builder()
                .name(RandomData.getName())
                .build();

        var response = new ChangeNameRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .post(request).extract().as(ChangeNameResponse.class);

        softly.assertThat(response.getMessage()).isEqualTo("Profile updated successfully");
    }
}
