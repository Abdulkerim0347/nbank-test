package iteration1;

import generators.RandomData;
import models.BaseAccountResponse;
import models.CreateUserRequest;
import models.UserRole;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
import requests.CreateAccountRequester;
import requests.GetAccountsRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateAccountTest extends BaseTest {

    @Test
    public void userCanCreateAccountTest() {
        // stores username, password and role
        var userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        // REST assured request
        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(), // admin auth
                ResponseSpecs.entityWasCreated()) // checking status code
                .post(userRequest);

        var account = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null).extract().as(BaseAccountResponse.class);

        var accounts = new GetAccountsRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .post(null)
                .extract()
                .jsonPath()
                .getList("", BaseAccountResponse.class);

        assertTrue(accounts.stream().anyMatch(a -> a.getId() == account.getId()),
                "Created account should exist in the account list");
    }
}
