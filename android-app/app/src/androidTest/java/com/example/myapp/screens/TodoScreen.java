package com.example.myapp.screens;

import com.example.myapp.R;
import androidx.recyclerview.widget.RecyclerView;
import static org.junit.Assert.assertEquals;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

public class TodoScreen extends Screen {
    public void title(String value) { onView(withId(R.id.todoNoteNameEditText)).perform(replaceText(value), closeSoftKeyboard()); }
    public void add(String value) { onView(withId(R.id.newTodoItemEditText)).perform(replaceText(value), closeSoftKeyboard()); onView(withId(R.id.addTodoItemButton)).perform(click()); }
    public void save() { onView(withId(R.id.saveTodoNoteButton)).perform(click()); }
    public void assertItem(String value) { waitFor(allOf(withId(R.id.todoTextView), withText(value))); onView(allOf(withId(R.id.todoTextView), withText(value))).check(matches(isDisplayed())); }
    public void check(String value) { assertItem(value); onView(allOf(withId(R.id.todoCheckBox), hasSibling(withText(value)))).perform(click()); }
    public void assertChecked(String value) { assertItem(value); onView(allOf(withId(R.id.todoCheckBox), hasSibling(withText(value)))).check(matches(isChecked())); }
    public void delete(String value) { assertItem(value); onView(allOf(withId(R.id.deleteTodoItemButton), hasSibling(withText(value)))).perform(click()); }
    public void assertMissing(String value) { onView(withId(R.id.todoItemsRecyclerView)).check(matches(not(hasDescendant(withText(value))))); }
    public void assertItemCount(int count) {
        onView(withId(R.id.todoItemsRecyclerView)).check((view, error) -> {
            if (error != null) throw error;
            assertEquals(count, ((RecyclerView) view).getAdapter().getItemCount());
        });
    }
    public void assertTitleError() { onView(withId(R.id.todoTitleErrorTextView)).check(matches(allOf(isDisplayed(), withText(R.string.title_required)))); }
}
