package com.notes.framework.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static com.notes.framework.constants.PageConstants.*;

public class NotesListPage extends BasePage {
    @AndroidFindBy(id = NOTES_LIST) private WebElement notesList;
    @AndroidFindBy(accessibility = MORE_OPTIONS) private WebElement moreOptions;
    @AndroidFindBy(xpath = NOTES_TITLE) private WebElement title;
    @AndroidFindBy(id = ADD_NOTE) private WebElement addNote;
    @AndroidFindBy(xpath = TEXT_OPTION) private WebElement textOption;
    @AndroidFindBy(xpath = TODO_OPTION) private WebElement todoOption;
    @AndroidFindBy(id = NOTE_NAME) private List<WebElement> noteNames;
    @AndroidFindBy(xpath = SORT_BY_TITLE) private WebElement sortByTitle;
    // The original APK lacked deletion; the updated APK adds this functionality.
    /*
    @AndroidFindBy(id = CONFIRM_DELETE) private WebElement confirmDelete;
    @AndroidFindBy(id = DELETE_MESSAGE) private WebElement deleteMessage;
    */
    private final int waitSeconds;

    public NotesListPage(AndroidDriver driver, int waitSeconds) {
        super(driver, waitSeconds);
        this.waitSeconds = waitSeconds;
    }

    public void returnToNotesScreen() {
        new WebDriverWait(driver, Duration.ofSeconds(waitSeconds)).until(d ->
                visible(By.id(NOTES_LIST)) || visible(By.xpath(SORT_BY_TITLE)) || visible(By.id(ALERT_TITLE))
                        || visible(By.id(TEXT_TITLE)) || visible(By.id(TODO_TITLE)) || visible(By.xpath(TODO_SCREEN_TITLE))
                        || visible(By.xpath(NOTE_SCREEN_TITLE)));
        for (int attempt = 0; attempt < 5; attempt++) {
            if (visible(By.xpath(SORT_BY_TITLE)) || visible(By.id(ALERT_TITLE))) {
                driver.navigate().back();
            } else if (visible(By.id(NOTES_LIST)) && visible(By.id(ADD_NOTE))) {
                return;
            } else if (visible(By.id(TEXT_TITLE)) || visible(By.id(TODO_TITLE)) || visible(By.xpath(TODO_SCREEN_TITLE))
                    || visible(By.xpath(NOTE_SCREEN_TITLE))) {
                driver.navigate().back();
            } else {
                break;
            }
        }
        if (!visible(By.id(NOTES_LIST)) || !visible(By.id(ADD_NOTE)) || visible(By.xpath(SORT_BY_TITLE)) || visible(By.id(ALERT_TITLE))) {
            throw new IllegalStateException("Could not return to the Notes screen");
        }
    }

    private boolean visible(By locator) {
        return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean isNotesListVisible() { return shown(notesList).isDisplayed(); }
    public boolean isMoreOptionsVisible() { return shown(moreOptions).isDisplayed(); }
    public boolean isTitleVisible() { return shown(title).isDisplayed(); }
    public boolean isAddNoteVisible() { return shown(addNote).isDisplayed(); }
    public void openAddMenu() { click(addNote); }
    public boolean isTextOptionVisible() { return shown(textOption).isDisplayed(); }
    public boolean isTodoOptionVisible() { return shown(todoOption).isDisplayed(); }
    public void selectTextNote() { click(textOption); }
    public void selectTodoList() { click(todoOption); }
    public void sortByTitle() { click(moreOptions); click(sortByTitle); }
    public void openNote(String name) { click(noteTitle(name)); }

    /* The following delete methods require the updated APK.
    public String getNoteName(String name) { return find(noteTitle(name)).getText(); }

    public void openDeleteDialog(String name) {
        click(By.xpath("//android.widget.TextView[@resource-id='" + NOTE_NAME + "' and @text='" + name
                + "']/following-sibling::android.widget.ImageButton[@resource-id='" + DELETE_NOTE + "']"));
    }

    public String getDeleteMessage() { return shown(deleteMessage).getText(); }

    public void confirmDelete(String name) {
        click(confirmDelete);
        new WebDriverWait(driver, Duration.ofSeconds(waitSeconds))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated(noteTitle(name)));
    }
    */

    public boolean isTodoVisible(String name) {
        return find(todoTitle(name)).isDisplayed();
    }

    public void openTodoPreview(String name) {
        find(todoTitle(name));
        click(noteSibling(name, NOTE_PREVIEW));
    }

    private By todoTitle(String name) {
        return By.xpath("//android.widget.TextView[@resource-id='" + NOTE_TYPE + "' and @text='TODO']"
                + "/following-sibling::android.widget.TextView[@resource-id='" + NOTE_NAME + "' and @text='" + name + "']");
    }

    public String getNotePreview(String name) {
        return find(noteSibling(name, NOTE_PREVIEW)).getText();
    }

    public List<String> getAllNoteNames() {
        while (scroll("up")) { }
        List<String> names = new ArrayList<>();
        boolean canScroll = true;
        while (true) {
            List<String> visible = new ArrayList<>();
            for (WebElement element : noteNames) {
                if (element.isDisplayed()) visible.add(element.getText());
            }
            int overlap = Math.min(names.size(), visible.size());
            while (overlap > 0 && !names.subList(names.size() - overlap, names.size()).equals(visible.subList(0, overlap))) {
                overlap--;
            }
            names.addAll(visible.subList(overlap, visible.size()));
            if (!canScroll) break;
            canScroll = scroll("down");
        }
        return names;
    }

    private boolean scroll(String direction) {
        String elementId = ((RemoteWebElement) find(By.id(NOTES_LIST))).getId();
        return (Boolean) driver.executeScript("mobile: scrollGesture", Map.of(
                "elementId", elementId, "direction", direction, "percent", 0.7));
    }

    public boolean isNoteVisible(String name) {
        shown(notesList);
        while (scroll("up")) { }
        boolean canScroll = true;
        while (true) {
            if (visible(noteTitle(name))) return true;
            if (!canScroll) return false;
            canScroll = scroll("down");
        }
    }

    public String getNoteDate(String name) {
        return find(noteSibling(name, NOTE_DATE)).getText();
    }

    private By noteSibling(String name, String id) {
        return By.xpath("//android.widget.TextView[@resource-id='" + NOTE_NAME + "' and @text='"
                + name + "']/following-sibling::android.widget.TextView[@resource-id='" + id + "']");
    }

    private By noteTitle(String name) {
        return By.xpath("//android.widget.TextView[@resource-id='" + NOTE_NAME + "' and @text='" + name + "']");
    }
}
