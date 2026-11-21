package com.example.myapp.screens;

import com.example.myapp.R;
import android.view.View;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static org.hamcrest.Matchers.allOf;

public class NotesScreen extends Screen {
    private Matcher<View> hasWindowFocus() {
        return new TypeSafeMatcher<View>() {
            @Override protected boolean matchesSafely(View view) { return view.hasWindowFocus(); }
            @Override public void describeTo(Description description) { description.appendText("window has focus"); }
        };
    }
    public void createText() { onView(withId(R.id.addNoteFab)).perform(click()); onView(withText(R.string.new_text_note_option)).perform(click()); }
    public void createTodo() { onView(withId(R.id.addNoteFab)).perform(click()); onView(withText(R.string.new_todo_list_option)).perform(click()); }
    public void open(String title) { assertVisible(title); onView(allOf(withId(R.id.noteNameTextView), withText(title), hasWindowFocus())).perform(click()); }
    public void assertVisible(String title) {
        waitFor(withId(R.id.notesRecyclerView));
        long end = System.currentTimeMillis() + 10000;
        do {
            try {
                onView(withId(R.id.notesRecyclerView)).inRoot(withDecorView(hasWindowFocus())).perform(scrollTo(hasDescendant(withText(title))));
                onView(allOf(withId(R.id.noteNameTextView), withText(title), hasWindowFocus())).check(matches(isDisplayed()));
                return;
            } catch (androidx.test.espresso.PerformException | androidx.test.espresso.NoMatchingViewException | androidx.test.espresso.AmbiguousViewMatcherException | AssertionError e) {
                try { Thread.sleep(100); } catch (InterruptedException interrupted) { Thread.currentThread().interrupt(); throw new AssertionError(interrupted); }
            }
        } while (System.currentTimeMillis() < end);
        throw new AssertionError("Note not found: " + title);
    }
    public void delete(String title) {
        assertVisible(title);
        onView(allOf(withId(R.id.deleteNoteButton), hasSibling(withText(title)))).perform(click());
        onView(withId(android.R.id.message)).check(matches(withText("Delete “" + title + "”?")));
        onView(withId(android.R.id.button1)).perform(click());
    }
}
