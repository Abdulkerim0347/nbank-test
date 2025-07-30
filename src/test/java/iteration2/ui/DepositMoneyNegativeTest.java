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
import ui.pages.DepositPage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DepositMoneyNegativeTest extends BaseUiTest {
    @Test
    public void userCannotDepositMoneyTest() {
        // pre steps on api
        var user = AdminSteps.createUser();
        var account = DepositSteps.createAccount(user);

        authAsUser(user);

        // UI deposit process
        final int MAX_DEPOSIT = 5000;
        int depositAmount = RandomData.getRandom().nextInt(5000) + MAX_DEPOSIT;
        new DepositPage().open().depositMoney(account, depositAmount)
                .checkAlertMessageAndAccept(BankAlert.PLEASE_DEPOSIT_LESS_THAN_OR_EQUAL_TO_5000.getMessage());

        // validate on API
        var updatedAccount = new CrudRequester(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .jsonPath()
                .getList("", BaseAccountResponse.class)
                .stream().filter(a -> a.getId() == account.getId())
                .findFirst().orElseThrow();

        assertThat(updatedAccount.getBalance()).isEqualTo(account.getBalance());
    }

    @Test
    public void userCannotDepositMoneyWithBlankAmountFieldTest() {
        // pre steps on api
        var user = AdminSteps.createUser();
        var account = DepositSteps.createAccount(user);

        authAsUser(user);

        // UI deposit process
        new DepositPage().open().depositMoneyNoAmount(account)
                .checkAlertMessageAndAccept(BankAlert.PLEASE_ENTER_A_VALID_AMOUNT.getMessage());

        // validate on API
        var updatedAccount = new CrudRequester(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .jsonPath()
                .getList("", BaseAccountResponse.class)
                .stream().filter(a -> a.getId() == account.getId())
                .findFirst().orElseThrow();

        assertThat(updatedAccount.getBalance()).isEqualTo(account.getBalance());
    }

    @Test
    public void userCannotDepositMoneyWithBlankAccountFieldTest() {
        // pre steps on api
        var user = AdminSteps.createUser();
        var account = DepositSteps.createAccount(user);

        authAsUser(user);

        // UI deposit process
        int depositAmount = RandomData.getRandom().nextInt(5000);
        new DepositPage().open().depositMoneyNoAccount(depositAmount)
                .checkAlertMessageAndAccept(BankAlert.PLEASE_SELECT_AN_ACCOUNT.getMessage());

        // validate on API
        var updatedAccount = new CrudRequester(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .jsonPath()
                .getList("", BaseAccountResponse.class)
                .stream().filter(a -> a.getId() == account.getId())
                .findFirst().orElseThrow();

        assertThat(updatedAccount.getBalance()).isEqualTo(account.getBalance());
    }
}
