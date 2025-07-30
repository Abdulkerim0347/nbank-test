package iteration2.api;

import generators.RandomData;
import iteration1.api.BaseTest;
import models.BaseAccountResponse;
import models.TransferMoneyRequest;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.steps.AdminSteps;
import requests.steps.DepositSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class TransferMoneyNegativeTest extends BaseTest {
    @Test
    public void userCannotTransferMoreThanBalance() {
        // generate random int between 1 and 50
        final int INITIAL_DEPOSIT = RandomData.getRandom().nextInt(50) + 1;
        // generate random int between 50 and 100
        final int TRANSFER_AMOUNT = RandomData.getRandom().nextInt(100) + 50;

        var userRequest = AdminSteps.createUser();

        // create 2 accounts
        var account1 = DepositSteps.createAccount(userRequest);
        var account2 = DepositSteps.createAccount(userRequest);

        // depositing some money on sender account
        DepositSteps.depositMoney(userRequest, account1, INITIAL_DEPOSIT);

        // money transfer request
        var transferRequest = TransferMoneyRequest.builder()
                .senderAccountId(account1.getId())
                .receiverAccountId(account2.getId())
                .amount(TRANSFER_AMOUNT)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsInvalidTransfer())
                .post(transferRequest);

        var updatedAccounts = new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .jsonPath()
                .getList("", BaseAccountResponse.class);

        var updatedSender = updatedAccounts.stream()
                .filter(a -> a.getId() == account1.getId())
                .findFirst()
                .orElseThrow();

        var updatedReceiver = updatedAccounts.stream()
                .filter(a -> a.getId() == account2.getId())
                .findFirst()
                .orElseThrow();

        softly.assertThat(updatedSender.getBalance()).isEqualTo(account1.getBalance() + INITIAL_DEPOSIT);
        softly.assertThat(updatedReceiver.getBalance()).isEqualTo(account2.getBalance());
    }
}
