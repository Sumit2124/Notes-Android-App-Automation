package com.notes.tests;

import com.notes.framework.pages.NotesListPage;
import java.util.UUID;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DeleteNoteTest extends BaseTest {
    /* The original APK had no delete control. This was fixed in the updated APK.
    @Test
    public void deleteTextNote() {
        NotesListPage notes = notesPage();
        String name = "Note " + UUID.randomUUID();
        createTextNote(notes, name, "Note for deletion");
        String displayedName = notes.getNoteName(name);
        Assert.assertEquals(displayedName, name, "The displayed note name is incorrect");
        notes.returnToNotesScreen();
        notes.openDeleteDialog(displayedName);
        Assert.assertEquals(notes.getDeleteMessage(), "Delete “" + displayedName + "”?", "Delete confirmation is incorrect");
        notes.confirmDelete(displayedName);
        Assert.assertFalse(notes.getAllNoteNames().contains(displayedName), "Deleted note is still visible");
    }
    */
}
