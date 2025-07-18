package iteration2.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import generators.RandomData;
import models.BaseAccountResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.steps.AdminSteps;
import requests.steps.DepositSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.Map;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferMoneyTest {
    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://172.18.0.1:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
    }

    @Test
    public void userCanTransferMoneyTest() {
        String defaultName = "noname";
        // generate random int between 500 and 1000
        final int INITIAL_DEPOSIT = RandomData.getRandom().nextInt(1001) + 500;
        // generate random int between 0 and 500
        final int TRANSFER_AMOUNT = RandomData.getRandom().nextInt(501);

        var user = AdminSteps.createUser();

        // create 2 accounts
        var accountSender = DepositSteps.createAccount(user);
        var accountReceiver = DepositSteps.createAccount(user);

        // depositing some money on sender account
        DepositSteps.depositMoney(user, accountSender, INITIAL_DEPOSIT);

        Selenide.open("/login");
        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(user.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(user.getPassword());
        $("button").click();

        $(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();

        $(Selectors.byClassName("form-control account-selector")).click();
        $$("ul li").filter(Condition.visible)
                .findBy(text(accountSender.getAccountNumber()))
                .click();

        $(Selectors.byAttribute("placeholder", "Enter recipient name")).sendKeys(defaultName);
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).sendKeys(accountReceiver.getAccountNumber());
        $(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(TRANSFER_AMOUNT + "");
        $("#confirmCheck").click();
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        assertEquals("✅ Successfully transferred $" + TRANSFER_AMOUNT + " to account " +  accountReceiver.getAccountNumber() + "!", alert.getText());
        alert.accept();

        // validate on API
        var updatedAccounts = new CrudRequester(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .jsonPath()
                .getList("", BaseAccountResponse.class);

        var updatedSender = updatedAccounts.stream()
                .filter(a -> a.getId() == accountSender.getId())
                .findFirst()
                .orElseThrow();

        var updatedReceiver = updatedAccounts.stream()
                .filter(a -> a.getId() == accountReceiver.getId())
                .findFirst()
                .orElseThrow();

        assertThat(updatedSender.getBalance()).isEqualTo(INITIAL_DEPOSIT - TRANSFER_AMOUNT);
        assertThat(updatedReceiver.getBalance()).isEqualTo(TRANSFER_AMOUNT);
    }

    @Test
    public void userCannotTransferMoneyTest() {
        String defaultName = "noname";
        // generate random int between 0 and 500
        final int INITIAL_DEPOSIT = RandomData.getRandom().nextInt(501);
        // generate random int between 500 and 1500
        final int EXCEEDED_TRANSFER_AMOUNT = RandomData.getRandom().nextInt(1001) + 500;

        var user = AdminSteps.createUser();

        // create 2 accounts
        var accountSender = DepositSteps.createAccount(user);
        var accountReceiver = DepositSteps.createAccount(user);

        // depositing some money on sender account
        DepositSteps.depositMoney(user, accountSender, INITIAL_DEPOSIT);

        Selenide.open("/login");
        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(user.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(user.getPassword());
        $("button").click();

        $(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();

        $(Selectors.byClassName("form-control account-selector")).click();
        $$("ul li").filter(Condition.visible)
                .findBy(text(accountSender.getAccountNumber()))
                .click();

        $(Selectors.byAttribute("placeholder", "Enter recipient name")).sendKeys(defaultName);
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).sendKeys(accountReceiver.getAccountNumber());
        $(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(EXCEEDED_TRANSFER_AMOUNT + "");
        $("#confirmCheck").click();
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        assertEquals("❌ Error: Invalid transfer: insufficient funds or invalid accounts", alert.getText());
        alert.accept();

        // validate on API
        var updatedAccounts = new CrudRequester(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .jsonPath()
                .getList("", BaseAccountResponse.class);

        var updatedSender = updatedAccounts.stream()
                .filter(a -> a.getId() == accountSender.getId())
                .findFirst()
                .orElseThrow();

        var updatedReceiver = updatedAccounts.stream()
                .filter(a -> a.getId() == accountReceiver.getId())
                .findFirst()
                .orElseThrow();

        assertThat(updatedSender.getBalance()).isEqualTo(INITIAL_DEPOSIT);
        assertThat(updatedReceiver.getBalance()).isEqualTo(accountReceiver.getBalance());
    }
}
