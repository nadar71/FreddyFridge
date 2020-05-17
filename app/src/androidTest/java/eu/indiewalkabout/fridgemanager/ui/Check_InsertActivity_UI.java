package eu.indiewalkabout.fridgemanager.ui;


import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CalendarView;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import eu.indiewalkabout.fridgemanager.R;
import eu.indiewalkabout.fridgemanager.SingletonProvider;
import eu.indiewalkabout.fridgemanager.data.DateConverter;
import eu.indiewalkabout.fridgemanager.data.FoodDatabase;
import eu.indiewalkabout.fridgemanager.data.FoodDbDao;
import eu.indiewalkabout.fridgemanager.util.DateUtility;
import eu.indiewalkabout.fridgemanager.util.TestUtility;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static org.hamcrest.Matchers.allOf;

import java.util.Date;
import java.util.Calendar;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class Check_InsertActivity_UI {
    MainActivity mainActivity;
    InsertFoodActivity insertActivity;
    FoodListActivity   foodListActivity;
    private final String TAG = Check_InsertActivity_UI.class.getName();
    private String targetText = "";
    FoodDatabase foodDatabase;
    FoodDbDao foodDbDao;

    @Rule
    public ActivityTestRule<InsertFoodActivity> insertFoodActivityTestRule = new ActivityTestRule<>(InsertFoodActivity.class);

    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule = new ActivityTestRule(MainActivity.class);

    @Before
    public void init(){
        mainActivity   = mainActivityRule.getActivity();
        insertActivity = insertFoodActivityTestRule.getActivity();
        foodDatabase   = ((SingletonProvider) SingletonProvider.Companion.getsContext()).getDatabase();
        foodDbDao      = foodDatabase.foodDbDao();
    }

    @Test
    public void checkButtonExists() {

        onView(withId(R.id.insert_food_fab)).perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.expiring_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(RecyclerView.class),
                                                3)),
                                1),
                        isDisplayed()));
        textView.check(matches(withText(R.string.expiring_label)));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.consumed_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(RecyclerView.class),
                                                2)),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText(R.string.consumed_label)));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.wasted_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(RecyclerView.class),
                                                1)),
                                1),
                        isDisplayed()));
        textView3.check(matches(withText(R.string.wasted_label)));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.home_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(RecyclerView.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView4.check(matches(withText(R.string.home_label)));
    }




    @Test
    // Press button expiring food and open expiring food list activity
    public void openExpiringList() {

        Intents.init();
        onView(withId(R.id.insert_food_fab)).perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.expiring_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(RecyclerView.class),
                                                3)),
                                1),
                        isDisplayed()));
        textView.perform(click());

        intended(hasComponent(FoodListActivity.class.getName()));
        Intents.release();
        onView(withId(R.id.toolbar_title_tv)).check(matches(withText(R.string.foodExpiring_activity_title)));

    }

    @Test
    // Press button consumed food and open consumed food list activity
    public void openConsumedList() {

        Intents.init();
        onView(withId(R.id.insert_food_fab)).perform(click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.consumed_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(RecyclerView.class),
                                                2)),
                                1),
                        isDisplayed()));
        textView2.perform(click());

        intended(hasComponent(FoodListActivity.class.getName()));
        Intents.release();
        onView(withId(R.id.toolbar_title_tv)).check(matches(withText(R.string.foodSaved_activity_title)));
    }


    @Test
    // Press button wasted food and open wasted food list activity
    public void openWastedList() {

        Intents.init();
        onView(withId(R.id.insert_food_fab)).perform(click());

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.wasted_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(RecyclerView.class),
                                                1)),
                                1),
                        isDisplayed()));
        textView3.perform(click());

        intended(hasComponent(FoodListActivity.class.getName()));
        Intents.release();
        onView(withId(R.id.toolbar_title_tv)).check(matches(withText(R.string.foodDead_activity_title)));
    }


    @Test
    // Press button home  and open home activity
    public void openHome() {

        Intents.init();
        onView(withId(R.id.insert_food_fab)).perform(click());

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.home_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(RecyclerView.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView4.perform(click());

        intended(hasComponent(MainActivity.class.getName()));
        Intents.release();
    }



    @Test
    // Insert a a food expiring today and come back to main activity
    public void testInsertExpiringToday() {
        // reset db
        foodDbDao.dropTable();
        targetText = "food_expiring_today";

        // init intent for check intent
        Intents.init();
        onView(allOf(withId(R.id.foodName_et))).perform(replaceText(targetText), closeSoftKeyboard());
        onView(allOf(withId(R.id.foodName_et), withText(targetText))).perform(pressImeActionButton());

        onView(withId(R.id.save_btn)).perform(click());
        onView(withId(R.id.insert_food_fab)).perform(click());

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.home_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(RecyclerView.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView4.perform(click());

        intended(hasComponent(MainActivity.class.getName()));
        Intents.release();

        if (TestUtility.doesViewExist(R.id.skip)){
            onView(withId(R.id.skip)).perform(click());
        }
        /* debug :
        onView(ViewMatchers.withId(R.id.main_today_food_list_recycleView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        */

        // check presence of food inserted with expiring date today
        RecyclerView recyclerView = mainActivity.findViewById(R.id.main_today_food_list_recycleView);
        int itemCount             = recyclerView.getChildCount();
        for(int i = 0; i<itemCount; i++) {
            ViewHolder holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
            TextView v        = holder.itemView.findViewById(R.id.foodName_tv);
            if (targetText.equals(v.getText())) {
                // Log.d(TAG, "testInsertExpiringToday: find the target text : "+targetText);
                System.out.println("testInsertExpiringToday: find the target text : "+targetText);

                onView(ViewMatchers.withId(R.id.main_today_food_list_recycleView))
                        // .check(matches(atPositionOnView(0, withText("food_expiring_today"), R.id.foodName_tv)));
                        // .check(matches(atPositionOnView(0, withId(R.id.foodName_tv), R.id.foodName_tv)))
                        .check(matches(atPositionOnView(i, withText(targetText), R.id.foodName_tv)));
                break;
            }
            onView(ViewMatchers.withId(R.id.main_today_food_list_recycleView))
                    .perform(RecyclerViewActions.scrollToPosition(i+1));
        }
    }


    @Test
    // Insert a a food already wasted and check
    public void testInsertWastedFood() {
        // reset db
        foodDbDao.dropTable();
        targetText = "wasted_food";

        // init intent for check intent
        Intents.init();
        onView(allOf(withId(R.id.foodName_et))).perform(replaceText(targetText), closeSoftKeyboard());
        onView(allOf(withId(R.id.foodName_et), withText(targetText))).perform(pressImeActionButton());

        // set test date
        insertActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // put an expiring date before today
                Calendar calendar = Calendar.getInstance();
                Date pastDate     = DateUtility.INSTANCE.addDays(new Date(),-1);
                calendar.setTime(pastDate);
                insertActivity.setDatePicked(calendar);
                insertActivity.setDateExpir_cv(pastDate);

                // set in the view too
                CalendarView view = (CalendarView) insertActivity.findViewById(R.id.calendar_cv);
                view.setDate(DateConverter.INSTANCE.fromDate(pastDate));
                System.out.println("**** DATA CHANGED *****");
            }

        });

        onView(withId(R.id.save_btn)).perform(click());
        onView(withId(R.id.insert_food_fab)).perform(click());


        ViewInteraction textView3 = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.wasted_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(RecyclerView.class),
                                                1)),
                                1),
                        isDisplayed()));
        textView3.perform(click());

        intended(hasComponent(FoodListActivity.class.getName()));
        Intents.release();
        onView(withId(R.id.toolbar_title_tv)).check(matches(withText(R.string.foodDead_activity_title)));

        onView(ViewMatchers.withId(R.id.food_list_recycleView))
                .check(matches(atPositionOnView(0, withText(targetText), R.id.foodName_tv)));
    }



    @Test
    // Insert a a food not expired and not expiring today, int he future
    public void testInsertExpiringInTheFutureFood() {
        // reset db
        foodDbDao.dropTable();
        targetText = "future_expiring_food";

        // init intent for check intent
        Intents.init();
        onView(allOf(withId(R.id.foodName_et))).perform(replaceText(targetText), closeSoftKeyboard());
        onView(allOf(withId(R.id.foodName_et), withText(targetText))).perform(pressImeActionButton());

        // set test date
        insertActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // put an expiring date before today
                Calendar calendar = Calendar.getInstance();
                Date pastDate     = DateUtility.INSTANCE.addDays(new Date(),3);
                calendar.setTime(pastDate);
                insertActivity.setDatePicked(calendar);
                insertActivity.setDateExpir_cv(pastDate);

                // set in the view too
                CalendarView view = (CalendarView) insertActivity.findViewById(R.id.calendar_cv);
                view.setDate(DateConverter.INSTANCE.fromDate(pastDate));
            }

        });

        onView(withId(R.id.save_btn)).perform(click());
        onView(withId(R.id.insert_food_fab)).perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.expiring_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(RecyclerView.class),
                                                3)),
                                1),
                        isDisplayed()));
        textView.perform(click());

        intended(hasComponent(FoodListActivity.class.getName()));
        Intents.release();
        onView(withId(R.id.toolbar_title_tv)).check(matches(withText(R.string.foodExpiring_activity_title)));

        onView(ViewMatchers.withId(R.id.food_list_recycleView))
                .check(matches(atPositionOnView(0, withText(targetText), R.id.foodName_tv)));
    }



    @Test
    // Move food from expiring list to consumed food list
    public void testMakeFoodConsumed() {
        // reset db
        foodDbDao.dropTable();
        targetText = "future_expiring_food";

        // init intent for check intent
        Intents.init();
        onView(allOf(withId(R.id.foodName_et))).perform(replaceText(targetText), closeSoftKeyboard());
        onView(allOf(withId(R.id.foodName_et), withText(targetText))).perform(pressImeActionButton());

        // set test date
        insertActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // put an expiring date before today
                Calendar calendar = Calendar.getInstance();
                Date pastDate     = DateUtility.INSTANCE.addDays(new Date(),3);
                calendar.setTime(pastDate);
                insertActivity.setDatePicked(calendar);
                insertActivity.setDateExpir_cv(pastDate);

                // set in the view too
                CalendarView view = (CalendarView) insertActivity.findViewById(R.id.calendar_cv);
                view.setDate(DateConverter.INSTANCE.fromDate(pastDate));
            }

        });

        onView(withId(R.id.save_btn)).perform(click());
        onView(withId(R.id.insert_food_fab)).perform(click());


        ViewInteraction textView = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.expiring_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(RecyclerView.class),
                                                3)),
                                1),
                        isDisplayed()));
        textView.perform(click());

        // check if it is in expiring food list
        intended(hasComponent(FoodListActivity.class.getName()));
        Intents.release();
        onView(withId(R.id.toolbar_title_tv)).check(matches(withText(R.string.foodExpiring_activity_title)));
        onView(ViewMatchers.withId(R.id.food_list_recycleView))
                .check(matches(atPositionOnView(0, withText(targetText), R.id.foodName_tv)));

        // set food as consumed
        onView(ViewMatchers.withId(R.id.consumed_cb)).perform(click());
        onView(ViewMatchers.withId(R.id.confirm_dialog_yes_btn)).perform(click());

        SystemClock.sleep(1000);

        // check if it is moved in consumed food list
        Intents.init();
        onView(withId(R.id.food_list_fab)).perform(click());

        ViewInteraction textView1 = onView(
                allOf(withId(R.id.txt_title_menu_item), withText(R.string.consumed_label),
                        childAtPosition(
                                allOf(withId(R.id.view_parent),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(RecyclerView.class),
                                                2)),
                                1),
                        isDisplayed()));
        textView1.perform(click());



        intended(hasComponent(FoodListActivity.class.getName()));
        Intents.release();
        onView(withId(R.id.toolbar_title_tv)).check(matches(withText(R.string.foodSaved_activity_title)));
        onView(ViewMatchers.withId(R.id.food_list_recycleView))
                .check(matches(atPositionOnView(0, withText(targetText), R.id.foodName_tv)));


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


    public static Matcher<View> atPositionOnView(final int position, final Matcher<View> itemMatcher,
                                                 @NonNull final int targetViewId) {

        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has view id " + itemMatcher + " at position " + position);
            }

            @Override
            public boolean matchesSafely(final RecyclerView recyclerView) {
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                View targetView = viewHolder.itemView.findViewById(targetViewId);
                return itemMatcher.matches(targetView);
            }
        };
    }
}
