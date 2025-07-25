package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class EditProfile extends BasePage<EditProfile> {
    private SelenideElement userInfo = $(Selectors.byClassName("user-info"));
    private SelenideElement enterNamePlaceholder = $(Selectors.byAttribute("placeholder", "Enter new name"));
    private SelenideElement button = $(Selectors.byText("\uD83D\uDCBE Save Changes"));

    @Override
    public String url() {
        return "/edit-profile";
    }

    public EditProfile changeName(String newName) {
        userInfo.click();
        enterNamePlaceholder.sendKeys(newName);
        button.click();
        return this;
    }
}
