package ui.pages;

import api.models.BaseAccountResponse;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class TransferPage extends BasePage<TransferPage> {
    private SelenideElement makeTransferText = $(Selectors.byText("\uD83D\uDD04 Make a Transfer"));
    private SelenideElement chooseAccountText = $(Selectors.byText("-- Choose an account --"));
    private ElementsCollection accountsOption = $$("option");
    private SelenideElement recepientNamePlaceholder = $(Selectors.byAttribute("placeholder", "Enter recipient name"));
    private SelenideElement recepientAccNumberPlaceholder = $(Selectors.byAttribute("placeholder", "Enter recipient account number"));
    private SelenideElement enterAmountPlaceholder = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private SelenideElement checkbox = $("#confirmCheck");
    private SelenideElement button = $(Selectors.byText("\uD83D\uDE80 Send Transfer"));

    @Override
    public String url() {
        return "/transfer";
    }

    public TransferPage makeTransfer(BaseAccountResponse accountSender, String recipientName, String accountReceiver, int transferAmount) {
        makeTransferText.click();
        chooseAccountText.click();
        accountsOption.filter(Condition.visible)
                .findBy(text(accountSender.getAccountNumber()))
                .click();
        recepientNamePlaceholder.sendKeys(recipientName);
        recepientAccNumberPlaceholder.sendKeys(accountReceiver);
        enterAmountPlaceholder.sendKeys(String.valueOf(transferAmount));
        checkbox.click();
        button.click();
        return this;
    }

    public TransferPage makeTransferBlankFields() {
        makeTransferText.click();
        button.click();
        return this;
    }
}
