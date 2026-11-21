package com.example.myapp;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.myapp.screens.NotesScreen;
import com.example.myapp.screens.TextNoteScreen;
import com.example.myapp.screens.TodoScreen;
import com.example.myapp.ui.noteslist.NoteListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import android.view.View;
import org.hamcrest.Matcher;
import java.util.List;
import com.example.myapp.data.model.Note;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4.class)
public class NotesEspressoTest {
    private ActivityScenario<MainActivity> activity;
    private final NotesScreen notes = new NotesScreen();
    private final TextNoteScreen text = new TextNoteScreen();
    private final TodoScreen todo = new TodoScreen();
    private String name;

    @Before public void setUp() {
        name = "Note " + UUID.randomUUID();
        activity = ActivityScenario.launch(MainActivity.class);
        NotesScreen.waitFor(withId(R.id.addNoteFab));
    }
    @After public void tearDown() { if (activity != null) activity.close(); }

    @Test public void createTextNoteAndVerifyContent() {
        notes.createText(); text.title(name); text.content("Content " + name); text.save();
        notes.assertVisible(name); notes.open(name); text.assertContent("Content " + name);
    }

    @Test public void createTodoAndVerifyItems() {
        notes.createTodo(); todo.title(name); todo.add("Eat Healthy"); todo.add("Drink Water");
        todo.add("Exercise"); todo.add("Read a Book"); todo.save();
        notes.assertVisible(name); notes.open(name);
        todo.assertItem("Eat Healthy"); todo.assertItem("Drink Water"); todo.assertItem("Exercise"); todo.assertItem("Read a Book");
    }

    @Test public void completeAndDeleteTodoItem() {
        notes.createTodo(); todo.title(name); todo.add("First item"); todo.add("Second item"); todo.check("First item"); todo.save();
        notes.assertVisible(name); notes.open(name); todo.assertChecked("First item");
        String updated = "Todo " + UUID.randomUUID(); todo.title(updated);
        todo.delete("Second item"); todo.assertMissing("Second item"); todo.save();
        notes.assertVisible(updated); notes.open(updated); todo.assertChecked("First item"); todo.assertMissing("Second item");
    }

    @Test public void editTextNote() {
        notes.createText(); text.title(name); text.content("Original content"); text.save();
        notes.assertVisible(name); notes.open(name); text.assertContent("Original content");
        String updated = "Edited " + UUID.randomUUID(); text.title(updated); text.content("Updated content"); text.save();
        notes.assertVisible(updated); notes.open(updated); text.assertContent("Updated content");
    }

    @Test public void deleteTextNote() {
        notes.createText(); text.title(name); text.content("Delete me"); text.save(); notes.assertVisible(name); notes.delete(name);
        onView(withId(R.id.notesRecyclerView)).check(matches(not(hasDescendant(withText(name)))));
    }

    @Test public void sortNotesByTitle() {
        String first = "A " + UUID.randomUUID();
        String last = "Z " + UUID.randomUUID();
        notes.createText(); text.title(first); text.content("First"); text.save(); notes.assertVisible(first);
        notes.createText(); text.title(last); text.content("Last"); text.save(); notes.assertVisible(last);
        onView(withContentDescription("More options")).perform(click());
        onView(withText("Sort by Title")).perform(click());
        onView(withId(R.id.notesRecyclerView)).perform(new ViewAction() {
            @Override public Matcher<View> getConstraints() { return isAssignableFrom(RecyclerView.class); }
            @Override public String getDescription() { return "wait for notes sorted by title"; }
            @Override public void perform(UiController controller, View view) {
                long end = System.currentTimeMillis() + 10000;
                do {
                    List<Note> items = ((NoteListAdapter) ((RecyclerView) view).getAdapter()).getCurrentList();
                    boolean foundFirst = false, foundLast = false, sorted = true;
                    for (int i = 0; i < items.size(); i++) {
                        String title = items.get(i).getName();
                        foundFirst |= first.equals(title);
                        foundLast |= last.equals(title);
                        if (i > 0 && items.get(i - 1).getName().compareTo(title) > 0) sorted = false;
                    }
                    if (foundFirst && foundLast && sorted) return;
                    controller.loopMainThreadForAtLeast(100);
                } while (System.currentTimeMillis() < end);
                throw new AssertionError("Notes are not sorted by title");
            }
        });
    }

    @Test public void emptyTextTitleShowsError() {
        notes.createText(); text.save(); text.assertTitleError(); text.title("   "); text.save(); text.assertTitleError();
    }

    @Test public void emptyTodoTitleShowsError() {
        notes.createTodo(); todo.save(); todo.assertTitleError(); todo.title("   "); todo.save(); todo.assertTitleError();
    }

    @Test public void emptyTodoItemsAreNotAdded() {
        notes.createTodo(); todo.add("Valid item"); todo.assertItem("Valid item"); todo.add(""); todo.add("   ");
        todo.assertItem("Valid item"); todo.assertItemCount(1);
    }
}
