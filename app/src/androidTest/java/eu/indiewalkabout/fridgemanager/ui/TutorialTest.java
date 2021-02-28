package eu.indiewalkabout.fridgemanager.ui;


import android.os.SystemClock;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.indiewalkabout.fridgemanager.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class TutorialTest {
    private MainActivity activity;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);


    @Before
    public void setUp() {
        activity   = mActivityTestRule.getActivity();

        // set consentSDK avoid showing consent banner
        activity.setConsentSDKNeed(false);

        // setting preferences to be sure to show every time the splash helper presentation
        activity.setAppOpenings(0);

        // Otherwise the workaround would be :
        // - delete preferences on device app
        // - launch one time and give admob consent
    }

    @Test
    public void init(){ }

    @Test
    // Check the closing of tutorial and the accessibility of main activity
    public void closeTutorialTest() {

        // press go to main activity from splash tutorial
        onView(withId(R.id.skip)).perform(click());

        // press floating button
        onView(withId(R.id.main_fab)).perform(click());

    }



    @Test
    // Check the viewing of tutorial and the accessibility of main activity
    public void usingTutorialTest() {
        onView(withId(R.id.next)).perform(click());
        onView(withId(R.id.next)).perform(click());
        onView(withId(R.id.done)).perform(click());

        SystemClock.sleep(1000);

        // press floating button
        onView(withId(R.id.main_fab)).perform(click());
        activity.setAppOpenings(4);
    }


    @Test
    // No tutorial presents after 2 openings
    public void tutorial_after_2_Openings() {
        onView(withId(R.id.today_expiring_list_title)).check(matches(withText(R.string.main_list_title)));
        onView(withId(R.id.main_fab)).perform(click());
    }


}
