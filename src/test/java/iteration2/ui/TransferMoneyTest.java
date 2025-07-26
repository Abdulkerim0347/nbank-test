package iteration2.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import generators.RandomData;
import models.BaseAccountResponse;
import models.LoginUserRequest;
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
//        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://localhost:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
    }

    final String DEFAULT_NAME = "noname";
    final int MAX_DEPOSIT = 5000;
    final int TRANSFER_LIMIT = 10000;

    @Test
    public void userCanTransferMoneyLessThanTenThousandTest() {
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

        var userAuthHeader = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK())
                .post(LoginUserRequest.builder().username(user.getUsername()).password(user.getPassword()).build())
                .extract()
                .header("Authorization");

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
        Selenide.open("/dashboard");

        $(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();

        $(Selectors.byText("-- Choose an account --")).click();
        $$("option").filter(Condition.visible)
                .findBy(text(accountSender.getAccountNumber()))
                .click();

        $(Selectors.byAttribute("placeholder", "Enter recipient name")).sendKeys(DEFAULT_NAME);
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
    public void userCannotTransferMoneyToWrongAccountTest() {
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

        var userAuthHeader = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK())
                .post(LoginUserRequest.builder().username(user.getUsername()).password(user.getPassword()).build())
                .extract()
                .header("Authorization");

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
        Selenide.open("/dashboard");

        $(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();

        $(Selectors.byText("-- Choose an account --")).click();
        $$("option").filter(Condition.visible)
                .findBy(text(accountSender.getAccountNumber()))
                .click();

        $(Selectors.byAttribute("placeholder", "Enter recipient name")).sendKeys(DEFAULT_NAME);
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).sendKeys(RandomData.getRandom().nextInt() + "");
        $(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(TRANSFER_AMOUNT + "");
        $("#confirmCheck").click();
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        assertEquals("❌ No user found with this account number.", alert.getText());
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

    @Test
    public void userCannotTransferMoneyWithInsufficientFundsTest() {
        // generate random int between 0 and 500
        final int INITIAL_DEPOSIT = RandomData.getRandom().nextInt(501);
        // generate random int between 500 and 1500
        final int INVALID_TRANSFER_AMOUNT = RandomData.getRandom().nextInt(1001) + 500;

        var user = AdminSteps.createUser();

        // create 2 accounts
        var accountSender = DepositSteps.createAccount(user);
        var accountReceiver = DepositSteps.createAccount(user);

        // depositing some money on sender account
        DepositSteps.depositMoney(user, accountSender, INITIAL_DEPOSIT);

        var userAuthHeader = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK())
                .post(LoginUserRequest.builder().username(user.getUsername()).password(user.getPassword()).build())
                .extract()
                .header("Authorization");

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
        Selenide.open("/dashboard");

        $(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();

        $(Selectors.byText("-- Choose an account --")).click();
        $$("option").filter(Condition.visible)
                .findBy(text(accountSender.getAccountNumber()))
                .click();

        $(Selectors.byAttribute("placeholder", "Enter recipient name")).sendKeys(DEFAULT_NAME);
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).sendKeys(accountReceiver.getAccountNumber());
        $(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(INVALID_TRANSFER_AMOUNT + "");
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

    @Test
    public void userCannotTransferMoneyWithBlankFieldsTest() {
        var user = AdminSteps.createUser();

        var userAuthHeader = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK())
                .post(LoginUserRequest.builder().username(user.getUsername()).password(user.getPassword()).build())
                .extract()
                .header("Authorization");

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
        Selenide.open("/dashboard");

        $(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();

        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        assertEquals("❌ Please fill all fields and confirm.", alert.getText());
        alert.accept();
    }

    @Test
    public void userCannotTransferMoreThanTenThousandTest() {
        // generate random int between 10 000 and 11 000
        final int EXCEEDED_TRANSFER_AMOUNT = RandomData.getRandom().nextInt(1001) + TRANSFER_LIMIT;

        var user = AdminSteps.createUser();

        // create 2 accounts
        var accountSender = DepositSteps.createAccount(user);
        var accountReceiver = DepositSteps.createAccount(user);

        // depositing 15 000 on sender account
        DepositSteps.depositMoney(user, accountSender, MAX_DEPOSIT);
        DepositSteps.depositMoney(user, accountSender, MAX_DEPOSIT);
        DepositSteps.depositMoney(user, accountSender, MAX_DEPOSIT);

        var userAuthHeader = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK())
                .post(LoginUserRequest.builder().username(user.getUsername()).password(user.getPassword()).build())
                .extract()
                .header("Authorization");

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
        Selenide.open("/dashboard");

        $(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();

        $(Selectors.byText("-- Choose an account --")).click();
        $$("option").filter(Condition.visible)
                .findBy(text(accountSender.getAccountNumber()))
                .click();

        $(Selectors.byAttribute("placeholder", "Enter recipient name")).sendKeys(DEFAULT_NAME);
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).sendKeys(accountReceiver.getAccountNumber());
        $(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(EXCEEDED_TRANSFER_AMOUNT + "");
        $("#confirmCheck").click();
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        assertEquals("❌ Error: Transfer amount cannot exceed 10000", alert.getText());
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

        assertThat(updatedSender.getBalance()).isEqualTo(MAX_DEPOSIT * 3);
        assertThat(updatedReceiver.getBalance()).isEqualTo(accountReceiver.getBalance());
    }

    @Test
    public void userCanTransferTenThousandTest() {
        var user = AdminSteps.createUser();

        // create 2 accounts
        var accountSender = DepositSteps.createAccount(user);
        var accountReceiver = DepositSteps.createAccount(user);

        // depositing 10 000 on sender account
        DepositSteps.depositMoney(user, accountSender, MAX_DEPOSIT);
        DepositSteps.depositMoney(user, accountSender, MAX_DEPOSIT);

        var userAuthHeader = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK())
                .post(LoginUserRequest.builder().username(user.getUsername()).password(user.getPassword()).build())
                .extract()
                .header("Authorization");

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
        Selenide.open("/dashboard");

        $(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();

        $(Selectors.byText("-- Choose an account --")).click();
        $$("option").filter(Condition.visible)
                .findBy(text(accountSender.getAccountNumber()))
                .click();

        $(Selectors.byAttribute("placeholder", "Enter recipient name")).sendKeys(DEFAULT_NAME);
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).sendKeys(accountReceiver.getAccountNumber());
        $(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(TRANSFER_LIMIT + "");
        $("#confirmCheck").click();
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        assertEquals("✅ Successfully transferred $" + TRANSFER_LIMIT + " to account " +  accountReceiver.getAccountNumber() + "!", alert.getText());
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

        assertThat(updatedSender.getBalance()).isEqualTo(MAX_DEPOSIT * 2 - TRANSFER_LIMIT);
        assertThat(updatedReceiver.getBalance()).isEqualTo(TRANSFER_LIMIT);
    }
}
