package ui.pages;

import api.models.BaseAccountResponse;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DepositPage extends BasePage<DepositPage> {
    private SelenideElement depositMoneyText = $(Selectors.byText("\uD83D\uDCB0 Deposit Money"));
    private SelenideElement chooseAccountText = $(Selectors.byText("-- Choose an account --"));
    private ElementsCollection accountsOption = $$("option");
    private SelenideElement enterAmountPlaceholder = $("[placeholder='Enter amount']");
    private SelenideElement button = $(Selectors.byText("\uD83D\uDCB5 Deposit"));

    @Override
    public String url() {
        return "/deposit";
    }

    public DepositPage depositMoney(BaseAccountResponse account, int depositAmount) {
        depositMoneyText.click();
        chooseAccountText.click();
        accountsOption.filter(Condition.visible)
                .findBy(text(account.getAccountNumber()))
                .click();
        enterAmountPlaceholder.setValue(depositAmount + "");
        button.click();
        return this;
    }
}
