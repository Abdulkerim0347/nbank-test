package iteration2;

import generators.RandomData;
import iteration1.BaseTest;
import models.*;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
import requests.CreateAccountRequester;
import requests.DepositRequester;
import requests.TransferMoneyRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class TransferMoneyTest extends BaseTest {
    @Test
    public void userCanTransferMoneyTest() {
        // create 2 users
        var userRequest1 = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        var userRequest2 = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest1);

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest2);

        // create accounts for both users
        var account1 = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null).extract().as(DepositResponse.class);

        var account2 = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null).extract().as(DepositResponse.class);

        int initialDeposit = 100;
        int transferAmount = 50;

        // depositing some money on sender account
        var depositRequest = DepositRequest.builder()
                .id(account1.getId())
                .balance(initialDeposit)
                .build();

        new DepositRequester(
                RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .post(depositRequest);

        // money transfer request
        var transferRequest = TransferMoneyRequest.builder()
                .senderAccountId(account1.getId())
                .receiverAccountId(account2.getId())
                .amount(transferAmount)
                .build();

        var transferResponse = new TransferMoneyRequester(
                RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .post(transferRequest).extract().as(TransferMoneyResponse.class);

        softly.assertThat(transferResponse.getMessage()).isEqualTo("Transfer successful");
        softly.assertThat(transferResponse.getAmount()).isEqualTo(account2.getBalance() + transferAmount);
        softly.assertThat(transferResponse.getReceiverAccountId()).isEqualTo(transferRequest.getReceiverAccountId());
    }
}
