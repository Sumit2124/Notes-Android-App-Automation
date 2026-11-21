package com.notes.tests;

import com.notes.framework.config.Configuration;
import com.notes.framework.driver.DriverFactory;
import com.notes.framework.pages.NotesListPage;
import com.notes.framework.pages.TextNotePage;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.testng.Assert;
import org.testng.ITestResult;
import org.openqa.selenium.OutputType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public class BaseTest {
    protected AndroidDriver driver;
    protected Configuration config;

    @BeforeSuite
    public void installCleanApp() throws Exception {
        Configuration settings = new Configuration();
        if (!"local".equalsIgnoreCase(settings.required("execution"))) return;

        if (Boolean.getBoolean("regression.parallel")) {
            throw new IllegalStateException("Parallel regression requires cloud execution and separate devices; run local tests sequentially");
        }

        Path apk = Path.of(settings.required("local.apkPath")).toAbsolutePath();
        if (!Files.isRegularFile(apk)) {
            throw new IllegalArgumentException("APK not found: " + apk);
        }
        List<String> adb = new ArrayList<>();
        adb.add("adb");
        if (!settings.get("local.udid").isEmpty()) {
            adb.add("-s");
            adb.add(settings.get("local.udid"));
        }
        runAdb(adb, "install", "-r", "-d", apk.toString());
        runAdb(adb, "shell", "pm", "clear", settings.required("local.appPackage"));
    }

    private void runAdb(List<String> adb, String... arguments) throws Exception {
        List<String> command = new ArrayList<>(adb);
        command.addAll(List.of(arguments));
        int exitCode = new ProcessBuilder(command).inheritIO().start().waitFor();
        if (exitCode != 0) throw new IllegalStateException("ADB command failed: " + String.join(" ", command));
    }

    @BeforeMethod
    public void setUp() throws Exception {
        config = new Configuration();
        driver = DriverFactory.createDriver(config);
        notesPage().returnToNotesScreen();
    }

    protected int waitSeconds() {
        return Integer.parseInt(config.required("waitSeconds"));
    }

    protected NotesListPage notesPage() {
        return new NotesListPage(driver, waitSeconds());
    }

    protected NotesListPage openAddMenu() {
        NotesListPage notes = notesPage();
        Assert.assertTrue(notes.isNotesListVisible(), "Notes screen is not visible");
        Assert.assertTrue(notes.isMoreOptionsVisible(), "More options is not visible");
        Assert.assertTrue(notes.isTitleVisible(), "Notes title is not visible");
        Assert.assertTrue(notes.isAddNoteVisible(), "Add note button is not visible");
        notes.openAddMenu();
        Assert.assertTrue(notes.isTextOptionVisible(), "New Text Note option is not visible");
        Assert.assertTrue(notes.isTodoOptionVisible(), "New Todo List option is not visible");
        return notes;
    }

    protected void createTextNote(NotesListPage notes, String name, String content) {
        Assert.assertTrue(notes.isNotesListVisible(), "Notes screen is not visible");
        notes.openAddMenu();
        notes.selectTextNote();
        TextNotePage editor = new TextNotePage(driver, waitSeconds());
        editor.enterTitle(name);
        editor.enterContent(content);
        editor.save();
        Assert.assertTrue(notes.isNoteVisible(name), "Created note is not visible");
    }

    protected void assertSavedNote(NotesListPage notes, String name) {
        Assert.assertTrue(notes.isNotesListVisible(), "Did not return to the notes screen");
        Assert.assertTrue(notes.isNoteVisible(name), "Saved note is not visible: " + name);
        LocalDate deviceDate = LocalDate.parse(driver.getDeviceTime().substring(0, 10));
        String expectedDate = deviceDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH));
        Assert.assertEquals(notes.getNoteDate(name), expectedDate, "Saved note date does not match the device date");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (driver != null) {
            try {
                if (!result.isSuccess()) {
                    byte[] screenshot = driver.getScreenshotAs(OutputType.BYTES);
                    Allure.addAttachment("Failure screenshot", "image/png", new ByteArrayInputStream(screenshot), ".png");
                }
            } catch (Exception e) {
                System.err.println("Could not capture failure screenshot: " + e.getMessage());
            } finally {
                driver.quit();
                driver = null;
            }
        }
    }
}
