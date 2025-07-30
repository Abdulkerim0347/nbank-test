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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChangeNameTest extends BaseUiTest {
    @Test
    public void userCanChangeItsNameTest() throws InterruptedException {
        var user = AdminSteps.createUser();

        authAsUser(user);

        // generate new name for user
        String newName = RandomData.getName() + " A";

        new EditProfile().open().changeName(newName)
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage());

        Thread.sleep(1000);

        // validate on UI
        new UserDashboard().open().getWelcomeText()
                .shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, " + newName + "!"));

        // validate on API
        var updatedProfile = new ValidatedCrudRequester<BaseUserResponse>(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.GET_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get(null);

        assertEquals(updatedProfile.getName(), newName);
    }
}
