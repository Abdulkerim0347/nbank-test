package iteration2.ui;

import api.generators.RandomData;
import api.models.BaseAccountResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.DepositSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import iteration1.ui.BaseUiTest;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.TransferPage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TransferMoneyTest extends BaseUiTest {
    final String DEFAULT_NAME = "noname";

    @Test
    public void userCanTransferMoneyTest() {
        // generate random int between 500 and 1000
        final int INITIAL_DEPOSIT = RandomData.getRandom().nextInt(1001) + 500;
        // generate random int between 0 and 500
        final int TRANSFER_AMOUNT = RandomData.getRandom().nextInt(501);

        var user = AdminSteps.createUser();
        var accountSender = DepositSteps.createAccount(user);
        var accountReceiver = DepositSteps.createAccount(user);

        // depositing some money on sender account
        DepositSteps.depositMoney(user, accountSender, INITIAL_DEPOSIT);

        authAsUser(user);

        new TransferPage().open().makeTransfer(accountSender, DEFAULT_NAME, accountReceiver.getAccountNumber(), TRANSFER_AMOUNT)
                .checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_TRANSFERRED.getMessage());

        // validate on API
        var updatedAccounts = new CrudRequester(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK())
                .get(null).extract().jsonPath().getList("", BaseAccountResponse.class);

        var updatedSender = updatedAccounts.stream()
                .filter(a -> a.getId() == accountSender.getId()).findFirst().orElseThrow();

        var updatedReceiver = updatedAccounts.stream()
                .filter(a -> a.getId() == accountReceiver.getId()).findFirst().orElseThrow();

        assertThat(updatedSender.getBalance()).isEqualTo(INITIAL_DEPOSIT - TRANSFER_AMOUNT);
        assertThat(updatedReceiver.getBalance()).isEqualTo(TRANSFER_AMOUNT);
    }

    @Test
    public void userCanTransferMoneyEdgeCaseTest() {
        final int MAX_DEPOSIT = 5000;
        final int TRANSFER_LIMIT = 10000;


        var user = AdminSteps.createUser();
        var accountSender = DepositSteps.createAccount(user);
        var accountReceiver = DepositSteps.createAccount(user);

        // depositing 10 000 on sender account
        DepositSteps.depositMoney(user, accountSender, MAX_DEPOSIT);
        DepositSteps.depositMoney(user, accountSender, MAX_DEPOSIT);

        authAsUser(user);

        new TransferPage().open().makeTransfer(accountSender, DEFAULT_NAME, accountReceiver.getAccountNumber(), TRANSFER_LIMIT)
                .checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_TRANSFERRED.getMessage());

        // validate on API
        var updatedAccounts = new CrudRequester(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK())
                .get(null).extract().jsonPath().getList("", BaseAccountResponse.class);

        var updatedSender = updatedAccounts.stream()
                .filter(a -> a.getId() == accountSender.getId()).findFirst().orElseThrow();

        var updatedReceiver = updatedAccounts.stream()
                .filter(a -> a.getId() == accountReceiver.getId()).findFirst().orElseThrow();

        assertThat(updatedSender.getBalance()).isEqualTo(MAX_DEPOSIT * 2 - TRANSFER_LIMIT);
        assertThat(updatedReceiver.getBalance()).isEqualTo(TRANSFER_LIMIT);
    }
}
