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

    /*
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class,
            true ,   // Initial touch mode
            false ); // Lazily launch activity
    */

    /*
    @Before
    public void setUp() {
        activity = mActivityTestRule.getActivity();
        activity.mockAppOpenings(0);
        activity.mockCheckConsentActive(false);

        // lazily activity launch
        Intent startMainActivity = new Intent();
        startMainActivity.putExtra(activity.MOCK_VAR,"go");
        mActivityTestRule.launchActivity(startMainActivity);

        System.out.println();
    }
    */


    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {

        activity = mActivityTestRule.getActivity();
        /*
        activity.setAppOpenings(0);
        activity.mockCheckConsentActive(false);
        System.out.println();
        */
        monitorCurrentActivity();
    }

    private final Activity[] currentActivity = new Activity[1];
    private void monitorCurrentActivity() {
        mActivityTestRule.getActivity().getApplication()
                .registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(final Activity activity, final Bundle savedInstanceState) {
                        MainActivity mActivity = (MainActivity)activity;
                        mActivity.setAppOpenings(0);
                        mActivity.mockCheckConsentActive(false);
                        System.out.println("DONE check");
                    }

                    @Override
                    public void onActivityStarted(final Activity activity) {
                        /*
                        MainActivity mActivity = (MainActivity)activity;
                        mActivity.setAppOpenings(0);
                        mActivity.mockCheckConsentActive(false);
                        System.out.println("DONE check");
                        */
                    }

                    @Override
                    public void onActivityResumed(final Activity activity) {
                        currentActivity[0] = activity;
                    }

                    @Override
                    public void onActivityPaused(final Activity activity) { }

                    @Override
                    public void onActivityStopped(final Activity activity) { }

                    @Override
                    public void onActivitySaveInstanceState(final Activity activity, final Bundle outState) { }

                    @Override
                    public void onActivityDestroyed(final Activity activity) { }
                });
    }

    private Activity getCurrentActivity() {
        return currentActivity[0];
    }

    //----------------------------------------------------------------------------------------------
    // *** TEMPORARILY WORKAROUND : arguing how to mock, current mock doesn't work ***
    // BEFORE LAUNCHING THIS TEST :
    // - delete preferences on device app
    // - launch one time and give admob consent
    // Best solution could be recreate the activity after starting :
    // https://stackoverflow.com/questions/1397361/how-do-i-restart-an-android-activity
    //----------------------------------------------------------------------------------------------
    @Test
    public void mainActivityTest() {
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.skip),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom),
                                        1),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

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
