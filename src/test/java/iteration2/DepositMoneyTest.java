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
import requests.CreateAccountRequester;
import requests.DepositRequester;
import requests.LoginUserRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static io.restassured.RestAssured.given;

public class DepositMoneyTest extends BaseTest {

    @Test
    public void userCanDepositMoneyTest() {
        var userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        var account = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null).extract().as(DepositResponse.class);

        int depositAmount = 100;

        var depositRequest = DepositRequest.builder()
                .id(account.getId())
                .balance(depositAmount)
                .build();

        var response = new DepositRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .post(depositRequest).extract().as(DepositResponse.class);

        softly.assertThat(response.getBalance()).isEqualTo(account.getBalance() + depositAmount);
        softly.assertThat(response.getAccountNumber()).isNotNull();
        softly.assertThat(response.getId()).isNotNull();

    }
}
