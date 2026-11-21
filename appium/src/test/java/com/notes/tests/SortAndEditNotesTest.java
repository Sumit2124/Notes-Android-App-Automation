package com.notes.tests;

import com.notes.framework.pages.NotesListPage;
import com.notes.framework.pages.TextNotePage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SortAndEditNotesTest extends BaseTest {
    @Test
    public void sortNotesByTitle() {
        NotesListPage notes = notesPage();
        String firstName = "A Note " + UUID.randomUUID();
        String secondName = "Z Note " + UUID.randomUUID();
        createTextNote(notes, firstName, "First note");
        createTextNote(notes, secondName, "Second note");

        notes.sortByTitle();
        List<String> actual = notes.getAllNoteNames();
        Assert.assertTrue(actual.contains(firstName) && actual.contains(secondName), "Created notes were not found in the list");
        List<String> expected = new ArrayList<>(actual);
        Collections.sort(expected);
        Assert.assertEquals(actual, expected, "Notes are not sorted by title in ascending order");
    }

    @Test
    public void editTextNote() {
        NotesListPage notes = notesPage();
        String originalName = "Note " + UUID.randomUUID();
        String newName = "Note " + UUID.randomUUID();
        String originalContent = "A note to edit";
        createTextNote(notes, originalName, originalContent);

        String preview = notes.getNotePreview(originalName);
        Assert.assertEquals(preview, originalContent, "Original preview is incorrect");
        notes.openNote(originalName);
        TextNotePage editor = new TextNotePage(driver, waitSeconds());
        Assert.assertTrue(editor.isOpen(), "The text note did not open");
        Assert.assertEquals(editor.getContent(), originalContent, "Opened note content is incorrect");
        String updatedContent = preview + " edited";
        editor.enterTitle(newName);
        editor.enterContent(updatedContent);
        editor.save();

        Assert.assertTrue(notes.isNotesListVisible(), "Did not return to the notes screen");
        Assert.assertTrue(notes.isNoteVisible(newName), "Updated title is not visible");
        Assert.assertEquals(notes.getNotePreview(newName), updatedContent, "Updated preview is incorrect");
    }

}
