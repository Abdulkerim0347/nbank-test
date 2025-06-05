package iteration2;

import iteration1.BaseTest;
import models.CreateAccountResponse;
import models.DepositRequest;
import models.DepositResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class DepositMoneyTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(ints = {50, 1000000})
    public void userCanDepositMoneyTest(int depositAmount) {
        var userRequest = AdminSteps.createUser();

        var account = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);

        var depositRequest = DepositRequest.builder()
                .id(account.getId())
                .balance(depositAmount)
                .build();

        var response = new ValidatedCrudRequester<DepositResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(depositRequest);

        softly.assertThat(response.getBalance()).isEqualTo(account.getBalance() + depositAmount);
        softly.assertThat(response.getAccountNumber()).isEqualTo(account.getAccountNumber());
        softly.assertThat(response.getId()).isEqualTo(account.getId());
    }

}
