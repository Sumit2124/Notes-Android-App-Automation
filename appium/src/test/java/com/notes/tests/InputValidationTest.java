package com.notes.tests;

import com.notes.framework.pages.NotesListPage;
import com.notes.framework.pages.TextNotePage;
import com.notes.framework.pages.TodoNotePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class InputValidationTest extends BaseTest {
    @Test
    public void textNoteRequiresTitle() {
        NotesListPage notes = openAddMenu();
        notes.selectTextNote();
        TextNotePage note = new TextNotePage(driver, waitSeconds());
        note.save();
        Assert.assertTrue(note.isOpen(), "Text note editor closed for an empty title");
        Assert.assertEquals(note.getTitleError(), "Title cannot be empty", "Empty title error is missing");
        note.enterTitle("   ");
        note.save();
        Assert.assertTrue(note.isOpen(), "Text note editor closed for a whitespace title");
        Assert.assertEquals(note.getTitleError(), "Title cannot be empty", "Whitespace title error is missing");
    }

    @Test
    public void todoRequiresTitle() {
        NotesListPage notes = openAddMenu();
        notes.selectTodoList();
        TodoNotePage todo = new TodoNotePage(driver, waitSeconds());
        todo.save();
        Assert.assertTrue(todo.isOpen(), "To-do editor closed for an empty title");
        Assert.assertEquals(todo.getTitleError(), "Title cannot be empty", "Empty title error is missing");
        todo.enterTitle("   ");
        todo.save();
        Assert.assertTrue(todo.isOpen(), "To-do editor closed for a whitespace title");
        Assert.assertEquals(todo.getTitleError(), "Title cannot be empty", "Whitespace title error is missing");
    }

    @Test
    public void emptyTodoItemsAreNotAdded() {
        NotesListPage notes = openAddMenu();
        notes.selectTodoList();
        TodoNotePage todo = new TodoNotePage(driver, waitSeconds());
        todo.addItem("A valid item");
        Assert.assertEquals(todo.itemCount(), 1, "Valid item was not added");
        todo.addItem("");
        todo.addItem("   ");
        Assert.assertEquals(todo.itemCount(), 1, "Empty or whitespace item was added");
        Assert.assertTrue(todo.isItemVisible("A valid item"), "Original item is missing");
    }
}
