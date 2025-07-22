package ui.pages;

import com.codeborne.selenide.Selenide;

public abstract class BasePage<T extends BasePage> {
    public abstract String url();

    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

    public <T extends BasePage> T getPage(Class<T> pageClass) {
        return Selenide.page(pageClass);
    }
}
