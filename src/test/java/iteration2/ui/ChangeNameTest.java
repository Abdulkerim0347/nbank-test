package iteration2.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import api.generators.RandomData;
import api.models.BaseUserResponse;
import api.models.LoginUserRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.Map;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ChangeNameTest {
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

    @Test
    public void userCanChangeItsNameTest() throws InterruptedException {
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

        // generate new name for user
        String newName = RandomData.getName() + " A";
        $(Selectors.byClassName("user-info")).click();
        $(Selectors.byAttribute("placeholder", "Enter new name")).sendKeys(newName);
        $(Selectors.byText("\uD83D\uDCBE Save Changes")).click();

        Alert alert = switchTo().alert();
        assertEquals(alert.getText(), "✅ Name updated successfully!");
        alert.accept();

        Thread.sleep(1000);

        // validate on UI
        $(Selectors.byText("\uD83C\uDFE0 Home")).click();
        $(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, " + newName + "!"));

        // validate on API
        var updatedProfile = new ValidatedCrudRequester<BaseUserResponse>(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.GET_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get(null);

        assertEquals(updatedProfile.getName(), newName);
    }

    @Test
    public void userCannotChangeItsNameTest() {
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

        // generate invalid name for user
        String newName = RandomData.getName();
        $(Selectors.byClassName("user-info")).click();
        $(Selectors.byAttribute("placeholder", "Enter new name")).sendKeys(newName);
        $(Selectors.byText("\uD83D\uDCBE Save Changes")).click();

        Alert alert = switchTo().alert();
        assertEquals(alert.getText(), "Name must contain two words with letters only");
        alert.accept();

        // validate on UI
        $(Selectors.byText("\uD83C\uDFE0 Home")).click();
        $(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));

        // validate on API
        var updatedProfile = new ValidatedCrudRequester<BaseUserResponse>(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.GET_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get(null);

        assertNotEquals(updatedProfile.getName(), newName);
    }
}
