package iteration2;

import generators.RandomData;
import iteration1.BaseTest;
import models.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.AdminCreateUserRequester;
import requests.CreateAccountRequester;
import requests.DepositRequester;
import requests.GetAccountsRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class DepositMoneyTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(ints = {50, 1000000})
    public void userCanDepositMoneyTest(int depositAmount) {
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
                .post(null).extract().as(BaseAccountResponse.class);

        var depositRequest = DepositRequest.builder()
                .id(account.getId())
                .balance(depositAmount)
                .build();

        var response = new DepositRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .post(depositRequest).extract().as(BaseAccountResponse.class);

        softly.assertThat(response.getBalance()).isEqualTo(depositAmount);
        softly.assertThat(response.getAccountNumber()).isNotBlank();
        softly.assertThat(response.getId()).isGreaterThan(0);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1000, 0})
    public void userCannotDepositInvalidAmountTest(int depositAmount) {
        final String ERROR = "Invalid account or amount";

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
                .post(null).extract().as(BaseAccountResponse.class);

        var depositRequest = DepositRequest.builder()
                .id(account.getId())
                .balance(depositAmount)
                .build();

        new DepositRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsBadRequest(ERROR))
                .post(depositRequest);

        var updatedAccount = new GetAccountsRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract().jsonPath().getList("", BaseAccountResponse.class)
                .stream().findFirst().get();

        softly.assertThat(account.getBalance()).isEqualTo(updatedAccount.getBalance());
    }
}
