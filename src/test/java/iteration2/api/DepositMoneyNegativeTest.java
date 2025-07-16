package iteration2.api;

import generators.RandomData;
import iteration1.api.BaseTest;
import models.BaseAccountResponse;
import models.DepositRequest;
import models.comparison.ModelAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.steps.AdminSteps;
import requests.steps.DepositSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class DepositMoneyNegativeTest extends BaseTest {
    @ParameterizedTest
    @ValueSource(ints = {-1000, 0})
    public void userCannotDepositInvalidAmountTest(int depositAmount) {
        var userRequest = AdminSteps.createUser();

        var account = DepositSteps.createAccount(userRequest);

        var depositRequest = DepositRequest.builder()
                .id(account.getId())
                .balance(depositAmount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsInvalidAccount())
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

    @Test
    public void userCannotExceedDepositLimitTest() {
        final double DEPOSIT_AMOUNT = RandomData.getRandom().nextDouble() + 5000;

        var userRequest = AdminSteps.createUser();

        var account = DepositSteps.createAccount(userRequest);

        var depositRequest = DepositRequest.builder()
                .id(account.getId())
                .balance(DEPOSIT_AMOUNT)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsDepositAmountExceedsLimit())
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
