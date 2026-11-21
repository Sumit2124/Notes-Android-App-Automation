package com.example.myapp.screens;

import com.example.myapp.R;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;

public class TextNoteScreen extends Screen {
    public void title(String value) { onView(withId(R.id.noteNameEditText)).perform(replaceText(value), closeSoftKeyboard()); }
    public void content(String value) { onView(withId(R.id.noteContentEditText)).perform(replaceText(value), closeSoftKeyboard()); }
    public void save() { onView(withId(R.id.saveNoteButton)).perform(click()); }
    public void assertContent(String value) { waitFor(withId(R.id.noteContentEditText)); onView(withId(R.id.noteContentEditText)).check(matches(withText(value))); }
    public void assertTitleError() { onView(withId(R.id.noteTitleErrorTextView)).check(matches(allOf(isDisplayed(), withText(R.string.title_required)))); }
}
