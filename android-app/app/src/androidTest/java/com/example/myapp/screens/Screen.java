package com.example.myapp.screens;

import android.view.View;
import android.view.ViewGroup;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import org.hamcrest.Matcher;
import java.util.ArrayDeque;
import java.util.Queue;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

public class Screen {
    public static void waitFor(Matcher<View> matcher) {
        onView(isRoot()).perform(new ViewAction() {
            @Override public Matcher<View> getConstraints() { return isRoot(); }
            @Override public String getDescription() { return "wait for " + matcher; }
            @Override public void perform(UiController controller, View root) {
                long end = System.currentTimeMillis() + 10000;
                do {
                    Queue<View> views = new ArrayDeque<>();
                    views.add(root);
                    while (!views.isEmpty()) {
                        View view = views.remove();
                        if (matcher.matches(view) && isDisplayed().matches(view)) return;
                        if (view instanceof ViewGroup) {
                            ViewGroup group = (ViewGroup) view;
                            for (int i = 0; i < group.getChildCount(); i++) views.add(group.getChildAt(i));
                        }
                    }
                    controller.loopMainThreadForAtLeast(100);
                } while (System.currentTimeMillis() < end);
                throw new AssertionError("View did not appear: " + matcher);
            }
        });
    }
}
