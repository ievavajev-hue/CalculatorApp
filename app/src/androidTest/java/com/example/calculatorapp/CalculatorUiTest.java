package com.example.calculatorapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CalculatorUiTest {

    @Rule
    public ActivityScenarioRule<MainActivity> rule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void twoPlusThreeEqualsFive_displays5() {
        onView(withId(R.id.btn2)).perform(click());
        onView(withId(R.id.btnPlus)).perform(click());
        onView(withId(R.id.btn3)).perform(click());
        onView(withId(R.id.btnEquals)).perform(click());

        onView(withId(R.id.txtDisplay)).check(matches(withText("5")));
    }
}
