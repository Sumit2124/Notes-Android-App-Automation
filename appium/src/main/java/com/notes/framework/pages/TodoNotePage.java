package com.notes.framework.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static com.notes.framework.constants.PageConstants.*;

public class TodoNotePage extends BasePage {
    private final int waitSeconds;
    @AndroidFindBy(id = TODO_TITLE) private WebElement title;
    @AndroidFindBy(id = TODO_NEW_ITEM) private WebElement newItem;
    @AndroidFindBy(id = TODO_ADD_ITEM) private WebElement addItem;
    @AndroidFindBy(id = TODO_SAVE) private WebElement save;
    @AndroidFindBy(id = TODO_ITEM) private List<WebElement> items;

    public TodoNotePage(AndroidDriver driver, int waitSeconds) {
        super(driver, waitSeconds);
        this.waitSeconds = waitSeconds;
    }

    public void enterTitle(String text) { type(title, text); }
    public String getTitleError() { return shown(title).getAttribute("error"); }
    public boolean isOpen() { return shown(title).isDisplayed(); }
    public void addItem(String text) { type(newItem, text); click(addItem); }
    public boolean isItemVisible(String text) {
        return find(item(text)).isDisplayed();
    }
    public void deleteItem(String text) {
        click(By.xpath("//android.widget.TextView[@resource-id='" + TODO_ITEM + "' and @text='" + text
                + "']/following-sibling::android.widget.ImageButton[@resource-id='" + DELETE_TODO_ITEM + "']"));
        new WebDriverWait(driver, Duration.ofSeconds(waitSeconds))
                .until(ExpectedConditions.invisibilityOfElementLocated(item(text)));
    }

    public boolean hasItem(String text) { return !driver.findElements(item(text)).isEmpty(); }

    private By item(String text) {
        return By.xpath("//android.widget.TextView[@resource-id='" + TODO_ITEM + "' and @text='" + text + "']");
    }
    public int itemCount() {
        find(By.id(TODO_ITEM));
        return items.size();
    }

    public void checkItem(int position) {
        WebElement checkbox = checkbox(position);
        if (!checkbox.isSelected()) checkbox.click();
    }

    public boolean isItemChecked(int position) {
        return "true".equals(checkbox(position).getAttribute("checked"));
    }

    private WebElement checkbox(int position) {
        return find(By.xpath("(//android.widget.TextView[@resource-id='" + TODO_ITEM + "'])["
                + position + "]/preceding-sibling::android.widget.CheckBox"));
    }
    public void save() { click(save); }
}
