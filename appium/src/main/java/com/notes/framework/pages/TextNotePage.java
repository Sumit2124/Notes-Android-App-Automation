package com.notes.framework.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import static com.notes.framework.constants.PageConstants.*;

public class TextNotePage extends BasePage {
    @AndroidFindBy(id = TEXT_TITLE) private WebElement title;
    @AndroidFindBy(id = TEXT_CONTENT) private WebElement content;
    @AndroidFindBy(id = TEXT_SAVE) private WebElement save;

    public TextNotePage(AndroidDriver driver, int waitSeconds) {
        super(driver, waitSeconds);
    }

    public void enterTitle(String text) { type(title, text); }
    public void enterContent(String text) { type(content, text); }
    public boolean isOpen() { return shown(content).isDisplayed(); }
    public String getContent() { return shown(content).getText(); }
    public String getTitleError() { return shown(title).getAttribute("error"); }
    public void save() { click(save); }
}
