package iteration2.ui;

import api.generators.RandomData;
import api.models.BaseUserResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import com.codeborne.selenide.Condition;
import iteration1.ui.BaseUiTest;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.EditProfile;
import ui.pages.UserDashboard;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ChangeNameNegativeTest extends BaseUiTest {
    @Test
    public void userCannotChangeItsNameTest() {
        var user = AdminSteps.createUser();

        authAsUser(user);

        // generate new name for user
        String newName = RandomData.getName();

        new EditProfile().open().changeName(newName)
                .checkAlertMessageAndAccept(BankAlert.NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY.getMessage(),
                        BankAlert.PLEASE_ENTER_A_VALID_NAME.getMessage());

        // validate on UI
        new UserDashboard().open().getWelcomeText()
                .shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));

        // validate on API
        var updatedProfile = new ValidatedCrudRequester<BaseUserResponse>(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.GET_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get(null);

        assertNotEquals(updatedProfile.getName(), newName);
    }

    @Test
    public void userCannotChangeItsNameToBlankTest() {
        var user = AdminSteps.createUser();

        authAsUser(user);

        // generate new name for user
        String newName = "";

        new EditProfile().open().changeName(newName)
                .checkAlertMessageAndAccept(BankAlert.NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY.getMessage(),
                        BankAlert.PLEASE_ENTER_A_VALID_NAME.getMessage());

        // validate on UI
        new UserDashboard().open().getWelcomeText()
                .shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));

        // validate on API
        var updatedProfile = new ValidatedCrudRequester<BaseUserResponse>(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.GET_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get(null);

        assertNotEquals(updatedProfile.getName(), newName);
    }
}
