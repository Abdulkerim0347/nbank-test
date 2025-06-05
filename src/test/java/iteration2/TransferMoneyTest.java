package iteration2;

import iteration1.BaseTest;
import models.*;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class TransferMoneyTest extends BaseTest {
    @Test
    public void userCanTransferMoneyTest() {
        var userRequest = AdminSteps.createUser();

        // create 2 accounts
        var account1 = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);

        var account2 = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);

        double initialDeposit = 100;
        double transferAmount = 50;

        // depositing some money on sender account
        var depositRequest = DepositRequest.builder()
                .id(account1.getId())
                .balance(initialDeposit)
                .build();

       new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(depositRequest);

        // money transfer request
        var transferRequest = TransferMoneyRequest.builder()
                .senderAccountId(account1.getId())
                .receiverAccountId(account2.getId())
                .amount(transferAmount)
                .build();

        var transferResponse = new ValidatedCrudRequester<TransferMoneyResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOK())
                .post(transferRequest);

        softly.assertThat(transferResponse.getMessage()).isEqualTo("Transfer successful");
        softly.assertThat(transferResponse.getReceiverAccountId()).isEqualTo(transferRequest.getReceiverAccountId());
        softly.assertThat(transferResponse.getAmount()).isEqualTo(account2.getBalance() + transferAmount);
//        softly.assertThat(account1.getBalance() + initialDeposit).isEqualTo(account1.getBalance() - transferAmount);
    }
}