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

public class TransferMoneyNegativeTest extends BaseUiTest {
    final String DEFAULT_NAME = "noname";

    @Test
    public void userCannotTransferMoneyLessThanDepositTest() {
        // generate random int between 0 and 500
        final int INITIAL_DEPOSIT = RandomData.getRandom().nextInt(501);
        // generate random int between 500 and 1500
        final int INVALID_TRANSFER_AMOUNT = RandomData.getRandom().nextInt(1001) + 500;

        var user = AdminSteps.createUser();
        var accountSender = DepositSteps.createAccount(user);
        var accountReceiver = DepositSteps.createAccount(user);

        // depositing some money on sender account
        DepositSteps.depositMoney(user, accountSender, INITIAL_DEPOSIT);

        authAsUser(user);

        new TransferPage().open().makeTransfer(accountSender, DEFAULT_NAME, accountReceiver.getAccountNumber(), INVALID_TRANSFER_AMOUNT)
                .checkAlertMessageAndAccept(BankAlert.ERROR_INVALID_TRANSFER.getMessage());

        // validate on API
        var updatedAccounts = new CrudRequester(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK())
                .get(null).extract().jsonPath().getList("", BaseAccountResponse.class);

        var updatedSender = updatedAccounts.stream()
                .filter(a -> a.getId() == accountSender.getId())
                .findFirst().orElseThrow();

        var updatedReceiver = updatedAccounts.stream()
                .filter(a -> a.getId() == accountReceiver.getId())
                .findFirst().orElseThrow();

        assertThat(updatedSender.getBalance()).isEqualTo(INITIAL_DEPOSIT);
        assertThat(updatedReceiver.getBalance()).isEqualTo(accountReceiver.getBalance());
    }

    @Test
    public void userCannotTransferMoneyWithBlankFieldsTest() {
        var user = AdminSteps.createUser();
        authAsUser(user);

        new TransferPage().open().makeTransferBlankFields()
                .checkAlertMessageAndAccept(BankAlert.PLEASE_FILL_ALL_FIELDS_AND_CONFIRM.getMessage());
    }

    @Test
    public void userCannotTransferMoneyToNonExistentAccountTest() {
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

        new TransferPage().open().makeTransfer(accountSender, DEFAULT_NAME, RandomData.getName(), TRANSFER_AMOUNT)
                .checkAlertMessageAndAccept(BankAlert.NO_USER_FOUND_WITH_THIS_ACCOUNT_NUMBER.getMessage());

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

        assertThat(updatedSender.getBalance()).isEqualTo(INITIAL_DEPOSIT);
        assertThat(updatedReceiver.getBalance()).isEqualTo(accountReceiver.getBalance());
    }
}