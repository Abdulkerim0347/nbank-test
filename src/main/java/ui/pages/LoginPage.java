package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {
    private SelenideElement usernameInput = $(Selectors.byAttribute("placeholder", "Username"));
    private SelenideElement passwordInput = $(Selectors.byAttribute("placeholder", "Password"));
    private SelenideElement button = $(Selectors.byId("loginButton"));

    @Override
    public String url() {
        return "/login";
    }

    public LoginPage login(String username, String password) {
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        button.click();
        return this;
    }
}
