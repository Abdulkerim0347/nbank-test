package iteration1.ui;

import api.generators.RandomModelGenerator;
import api.models.BaseUserResponse;
import api.models.CreateUserRequest;
import api.models.comparison.ModelAssertions;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import ui.pages.AdminPanel;
import ui.pages.BankAlert;

import java.util.Arrays;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateUserTest extends BaseUiTest {
    @Test
    public void adminCanCreateUserTest() {
        // login with admin
        var admin = CreateUserRequest.getAdmin();

        authAsUser(admin);

        // create user
        var newUser = RandomModelGenerator.generate(CreateUserRequest.class);

        new AdminPanel().open().createUser(newUser.getUsername(), newUser.getPassword())
                .checkAlertMessageAndAccept(BankAlert.USER_CREATED_SUCCESSFULLY.getMessage())
                .getAllUsers().findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldBe(Condition.visible);

        var createUser = AdminSteps.getAllUsers().stream()
                .filter(user -> user.getUsername().equals(newUser.getUsername()))
                .findFirst().get();

        ModelAssertions.assertThatModels(newUser, createUser).match();
    }

    @Test
    public void adminCannotCreateUserTest() {
        // login with admin
        var admin = CreateUserRequest.getAdmin();

        authAsUser(admin);

        // create user
        var newUser = RandomModelGenerator.generate(CreateUserRequest.class);
        newUser.setUsername("n");

        new  AdminPanel().open().createUser(newUser.getUsername(), newUser.getPassword())
                .checkAlertMessageAndAccept(BankAlert.USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS.getMessage())
                .getAllUsers().findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldNotBe(Condition.exist);


        long usersWithTheSameNameAsNewUser = AdminSteps.getAllUsers().stream()
                .filter(user -> user.getUsername().equals(newUser.getUsername()))
                .count();

        assertThat(usersWithTheSameNameAsNewUser).isZero();
    }
}
