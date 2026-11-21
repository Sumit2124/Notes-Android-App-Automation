package com.notes.tests;

import com.notes.framework.pages.NotesListPage;
import com.notes.framework.pages.TodoNotePage;
import java.util.UUID;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EditTodoTest extends BaseTest {
    @Test
    public void editTodoAndCompleteItems() {
        String originalName = "Todo " + UUID.randomUUID();
        String updatedName = "Todo " + UUID.randomUUID();
        String[] items = {"Eat Healthy", "Drink Water", "Exercise", "Read a Book"};

        NotesListPage notes = openAddMenu();
        notes.selectTodoList();
        TodoNotePage todo = new TodoNotePage(driver, waitSeconds());
        todo.enterTitle(originalName);
        for (String item : items) todo.addItem(item);
        todo.save();

        Assert.assertTrue(notes.isTodoVisible(originalName), "Created to-do list is not visible");
        notes.openTodoPreview(originalName);
        todo.enterTitle(updatedName);
        if (driver.isKeyboardShown()) driver.hideKeyboard();
        int count = todo.itemCount();
        Assert.assertEquals(count, items.length, "Unexpected number of to-do items");
        for (int i = 1; i <= count; i++) todo.checkItem(i);
        for (int i = 1; i <= count; i++) {
            Assert.assertTrue(todo.isItemChecked(i), "To-do item " + i + " is not checked");
        }
        todo.save();

        Assert.assertTrue(notes.isNotesListVisible(), "Did not return to the notes screen");
        Assert.assertTrue(notes.isTodoVisible(updatedName), "Updated to-do title is not visible");
        notes.openTodoPreview(updatedName);
        Assert.assertEquals(todo.itemCount(), items.length, "Saved to-do item count changed");
        for (int i = 1; i <= count; i++) {
            Assert.assertTrue(todo.isItemChecked(i), "Saved to-do item " + i + " is not checked");
        }

        // Delete one to-do item and verify the deletion remains after saving.
        todo.deleteItem(items[0]);
        Assert.assertFalse(todo.hasItem(items[0]), "Deleted to-do item is still visible");
        Assert.assertEquals(todo.itemCount(), items.length - 1, "Unexpected item count after deletion");
        todo.save();
        Assert.assertTrue(notes.isTodoVisible(updatedName), "Updated to-do list is not visible");
        notes.openTodoPreview(updatedName);
        Assert.assertEquals(todo.itemCount(), items.length - 1, "Deletion was not saved");
        Assert.assertFalse(todo.hasItem(items[0]), "Deleted to-do item returned after saving");
        for (int i = 1; i < items.length; i++) {
            Assert.assertTrue(todo.isItemVisible(items[i]), "Remaining to-do item is missing: " + items[i]);
        }
    }
}
