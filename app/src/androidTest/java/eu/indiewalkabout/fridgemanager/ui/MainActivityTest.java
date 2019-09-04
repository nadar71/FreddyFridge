package eu.indiewalkabout.fridgemanager.ui;


import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.indiewalkabout.fridgemanager.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    MainActivity activity;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        activity   = mActivityTestRule.getActivity();

        // mock consentSDK avoid showing consent banner
        activity.mockCheckConsentActive(false);

        // setting preferences to be sure to show every time the splash helper presentation
        activity.setAppOpenings(0);

        // Otherwise the workaround would be :
        // - delete preferences on device app
        // - launch one time and give admob consent
    }


    @Test
    public void mainActivityTest() {

        // press go to main activity from splash tutorial
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.skip),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom),
                                        1),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        // press floating button
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.main_fab),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                13),
                        isDisplayed()));
        floatingActionButton.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
