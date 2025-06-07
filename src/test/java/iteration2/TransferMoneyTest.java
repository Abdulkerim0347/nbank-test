package iteration2;

import generators.RandomData;
import iteration1.BaseTest;
import models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import requests.*;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class TransferMoneyTest extends BaseTest {
    @ParameterizedTest
    @CsvSource({
            "100.0, 50.0",
            "1000.0, 500.0"
    })
    public void userCanTransferMoneyTest(double initialDeposit, double transferAmount) {
        var userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        // create 2 accounts
        var account1 = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null).extract().as(BaseAccountResponse.class);

        var account2 = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null).extract().as(BaseAccountResponse.class);

        // depositing some money on sender account
        var depositRequest = DepositRequest.builder()
                .id(account1.getId())
                .balance(initialDeposit)
                .build();

        new DepositRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .post(depositRequest);

        // money transfer request
        var transferRequest = TransferMoneyRequest.builder()
                .senderAccountId(account1.getId())
                .receiverAccountId(account2.getId())
                .amount(transferAmount)
                .build();

        var transferResponse = new TransferMoneyRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .post(transferRequest).extract().as(TransferMoneyResponse.class);

        var updatedAccounts = new GetAccountsRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .post(null)
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

        softly.assertThat(updatedSender.getBalance()).isEqualTo(initialDeposit - transferAmount);
        softly.assertThat(updatedReceiver.getBalance()).isEqualTo(transferAmount);
        softly.assertThat(transferResponse.getReceiverAccountId()).isEqualTo(account2.getId());
    }

    @Test
    public void userCannotTransferMoreThanBalance() {
        final double INITIAL_DEPOSIT = 50;
        final double TRANSFER_AMOUNT = 100;

        var userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        var account1 = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null).extract().as(BaseAccountResponse.class);

        var account2 = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null).extract().as(BaseAccountResponse.class);

        var depositRequest = DepositRequest.builder()
                .id(account1.getId())
                .balance(INITIAL_DEPOSIT)
                .build();

        new DepositRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .post(depositRequest);

        var transferRequest = TransferMoneyRequest.builder()
                .senderAccountId(account1.getId())
                .receiverAccountId(account2.getId())
                .amount(TRANSFER_AMOUNT)
                .build();

        new TransferMoneyRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsBadRequest("Invalid transfer: insufficient funds or invalid accounts"))
                .post(transferRequest);

        var updatedAccounts = new GetAccountsRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
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

    // Баг: возвращает 200 при попытке перевести деньги со своего аккаунта на свой
    @Test
    public void userCannotTransferMoneyToTheSameAccountTest() {
        final double INITIAL_DEPOSIT = 200;
        final double TRANSFER_AMOUNT = 100;

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

        // depositing some money on sender account
        var depositRequest = DepositRequest.builder()
                .id(account.getId())
                .balance(INITIAL_DEPOSIT)
                .build();

        new DepositRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .post(depositRequest);

        // money transfer request
        var transferRequest = TransferMoneyRequest.builder()
                .senderAccountId(account.getId())
                .receiverAccountId(account.getId())
                .amount(TRANSFER_AMOUNT)
                .build();

        new TransferMoneyRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsBadRequest("Invalid account or amount"))
                .post(transferRequest);

        var updatedAccount = new GetAccountsRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .jsonPath()
                .getList("", BaseAccountResponse.class)
                .stream().findFirst().get();

        softly.assertThat(updatedAccount.getBalance()).isEqualTo(account.getBalance());
    }
}
