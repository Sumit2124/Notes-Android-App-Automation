package com.notes.tests;

import com.notes.framework.pages.NotesListPage;
import com.notes.framework.pages.TextNotePage;
import com.notes.framework.pages.TodoNotePage;
import java.util.UUID;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CreateNotesTest extends BaseTest {
    @Test
    public void createTextNote() {
        String name = "Note " + UUID.randomUUID();
        String content = "Random note " + UUID.randomUUID();
        NotesListPage notes = openAddMenu();
        notes.selectTextNote();

        TextNotePage textNote = new TextNotePage(driver, waitSeconds());
        textNote.enterTitle(name);
        textNote.enterContent(content);
        textNote.save();

        assertSavedNote(notes, name);
        notes.openNote(name);
        Assert.assertEquals(textNote.getContent(), content, "Saved note content did not persist");
    }

    @Test
    public void createTodoList() {
        String name = "Todo " + UUID.randomUUID();
        String[] items = {"Eat Healthy", "Drink Water", "Exercise", "Read a Book"};
        NotesListPage notes = openAddMenu();
        notes.selectTodoList();

        TodoNotePage todo = new TodoNotePage(driver, waitSeconds());
        for (String item : items) {
            todo.addItem(item);
        }
        for (String item : items) {
            Assert.assertTrue(todo.isItemVisible(item), "Todo item is not visible: " + item);
        }
        todo.enterTitle(name);
        todo.save();

        assertSavedNote(notes, name);
        notes.openTodoPreview(name);
        for (String item : items) {
            Assert.assertTrue(todo.isItemVisible(item), "Saved to-do item is missing: " + item);
        }
        Assert.assertEquals(todo.itemCount(), items.length, "Saved to-do list has an unexpected item count");
    }

}
