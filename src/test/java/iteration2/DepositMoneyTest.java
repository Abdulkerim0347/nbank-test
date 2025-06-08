package iteration2;

import iteration1.BaseTest;
import models.BaseAccountResponse;
import models.DepositRequest;
import models.comparison.ModelAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.steps.AdminSteps;
import requests.steps.DepositSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class DepositMoneyTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(ints = {50, 1000000})
    public void userCanDepositMoneyTest(int depositAmount) {
        var userRequest = AdminSteps.createUser();

        var account = DepositSteps.createAccount(userRequest);

        var depositResponse = DepositSteps.depositMoney(userRequest, account, depositAmount);

        softly.assertThat(depositResponse.getBalance()).isEqualTo(depositAmount);
        softly.assertThat(depositResponse.getAccountNumber()).isNotBlank();
        softly.assertThat(depositResponse.getId()).isGreaterThan(0);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1000, 0})
    public void userCannotDepositInvalidAmountTest(int depositAmount) {
        final String ERROR = "Invalid account or amount";

        var userRequest = AdminSteps.createUser();

        var account = DepositSteps.createAccount(userRequest);

        var depositRequest = DepositRequest.builder()
                .id(account.getId())
                .balance(depositAmount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsBadRequest(ERROR))
                .post(depositRequest);

        var updatedAccount = new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .jsonPath()
                .getList("", BaseAccountResponse.class)
                .stream().findFirst().get();

        ModelAssertions.assertThatModels(account, updatedAccount).match();
    }
}
