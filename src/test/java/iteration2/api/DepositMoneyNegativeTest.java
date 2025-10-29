package iteration2.api;

import api.models.BaseAccountResponse;
import api.models.DepositRequest;
import api.models.comparison.ModelAssertions;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.DepositSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import iteration1.api.BaseTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
                ResponseSpecs.requestReturnsInvalidAccountOrAmount())
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