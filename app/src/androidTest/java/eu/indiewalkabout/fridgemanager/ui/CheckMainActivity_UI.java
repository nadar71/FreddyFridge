package eu.indiewalkabout.fridgemanager.ui;



import android.os.SystemClock;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import junit.framework.Assert;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.indiewalkabout.fridgemanager.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CheckMainActivity_UI {
    private MainActivity activity;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);


    @Before
    public void setUp() {
        activity   = mActivityTestRule.getActivity();

        // set consentSDK avoid showing consent banner
        activity.setConsentSDKNeed(false);

        // setting preferences to be sure to show every time the splash helper presentation
        activity.setAppOpenings(4);

        // Otherwise the workaround would be :
        // - delete preferences on device app
        // - launch one time and give admob consent
    }



    @Test
    // Open app home, press fab button and check that all the other are present
    public void checkMenuButton_MainActivity() {

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.main_fab),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                13),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.add_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.support.v7.widget.RecyclerView.class),
                                                3)),
                                1),
                        isDisplayed()));
        textView.check(matches(withText(R.string.add_label)));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.expiring_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.support.v7.widget.RecyclerView.class),
                                                2)),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText(R.string.expiring_label)));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.wasted_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.support.v7.widget.RecyclerView.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView3.check(matches(withText(R.string.wasted_label)));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.consumed_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.support.v7.widget.RecyclerView.class),
                                                1)),
                                1),
                        isDisplayed()));
        textView4.check(matches(withText(R.string.consumed_label)));


        activity.setAppOpenings(4);

    }


    @Test
    // Press button new and open insert activity
    public void pressNew() {

        Intents.init();
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.main_fab),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                13),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.add_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.support.v7.widget.RecyclerView.class),
                                                3)),
                                1),
                        isDisplayed()));
        textView.perform(click());

        intended(hasComponent(InsertFoodActivity.class.getName()));
        Intents.release();
        onView(withId(R.id.save_btn)).check(matches(withText(R.string.save_btn_label)));


        activity.setAppOpenings(4);
    }




    @Test
    // Press button expiring food and open expiring food list activity
    public void pressExpiring() {

        Intents.init();
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.main_fab),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                13),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.expiring_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.support.v7.widget.RecyclerView.class),
                                                2)),
                                1),
                        isDisplayed()));
        textView2.perform(click());

        intended(hasComponent(FoodListActivity.class.getName()));
        Intents.release();
        onView(withId(R.id.toolbar_title_tv)).check(matches(withText(R.string.foodExpiring_activity_title)));


        activity.setAppOpenings(4);
    }



    @Test
    // Press button wasted food and open wasted food list activity
    public void pressWasted() {

        Intents.init();
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.main_fab),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                13),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.wasted_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.support.v7.widget.RecyclerView.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView3.perform(click());

        intended(hasComponent(FoodListActivity.class.getName()));
        Intents.release();
        onView(withId(R.id.toolbar_title_tv)).check(matches(withText(R.string.foodDead_activity_title)));

        activity.setAppOpenings(4);
    }


    @Test
    // Press button consumed food and open consumed food list activity
    public void pressConsumed() {

        Intents.init();
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.main_fab),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                13),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.consumed_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.support.v7.widget.RecyclerView.class),
                                                1)),
                                1),
                        isDisplayed()));
        textView4.perform(click());

        intended(hasComponent(FoodListActivity.class.getName()));
        Intents.release();
        onView(withId(R.id.toolbar_title_tv)).check(matches(withText(R.string.foodSaved_activity_title)));


        activity.setAppOpenings(4);
    }

    @Test
    // open settings
    public void openSettings(){
        Intents.init();
        onView(withId(R.id.settingsIcon_img)).perform(click());
        intended(hasComponent(MainSettingsActivity.class.getName()));
        Intents.release();
        onView(withId(R.id.settings_toolbar_title_tv)).check(matches(withText(R.string.mainSettingsActivity_title)));
    }


    @Test
    // open tutorial
    public void openTutorial(){
        Intents.init();
        onView(withId(R.id.help_img)).perform(click());
        intended(hasComponent(IntroActivity.class.getName()));
        Intents.release();
        onView(withId(R.id.skip));//.check(matches(withText(R.string.mainSettingsActivity_title)));
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
