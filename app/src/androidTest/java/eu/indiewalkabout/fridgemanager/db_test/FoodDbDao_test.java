package eu.indiewalkabout.fridgemanager.db_test;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import eu.indiewalkabout.fridgemanager.SingletonProvider;
import eu.indiewalkabout.fridgemanager.data.DateConverter;
import eu.indiewalkabout.fridgemanager.data.FoodDatabase;
import eu.indiewalkabout.fridgemanager.data.FoodDbDao;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;
import eu.indiewalkabout.fridgemanager.util.DateUtility;

import static org.junit.Assert.assertEquals;

/* TODO : fix error
Running the test, it ends with :
java.lang.NullPointerException: Attempt to invoke interface method 'java.lang.Object java.util.List.get(int)' on a null object reference

as if the foodList from LiveData<List> foodListLive is null.
foodListLive is null as well if I comment all what is after : Observer<List> observer = res -> assertEquals(1,res.size());

The pb happens both if I use this for creating db :
foodDatabase = ((SingletonProvider) SingletonProvider.Companion.getsContext()).getDatabase();

or this :
foodDatabase = Room.inMemoryDatabaseBuilder(
InstrumentationRegistry.getInstrumentation().getContext(),
FoodDatabase.class)
//.addMigrations(FoodDatabase.Companion.MIGRATION_1_2)
.build();

with only differences that in the first data are inserted in the physical db on device, so they are in, but no list is recovered in
 */
@RunWith(AndroidJUnit4.class)
public class FoodDbDao_test {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private FoodDatabase foodDatabase;
    private FoodDbDao foodDbDao;


    @Before
    public void createDb() {
/*
        foodDatabase = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().getContext(),
                FoodDatabase.class)
                //.addMigrations(FoodDatabase.Companion.MIGRATION_1_2)
                .build();

*/
        foodDatabase = ((SingletonProvider) SingletonProvider.Companion.getsContext()).getDatabase();
        foodDbDao = foodDatabase.foodDbDao();

    }

    @After
    public void closeDb() throws IOException {
        foodDatabase.close();
    }


    @Test
    public void insertFoodEntry_test() throws Exception {
        Date currentDate = new Date();
        FoodEntry food = new FoodEntry(0,"cheese", currentDate);

        foodDbDao.insertFoodEntry(food);

        LiveData<List<FoodEntry>> foodListLive = foodDbDao.loadAllFood(); //ERROR : Happens that is oddly null...

        Observer<List<FoodEntry>> observer = res -> assertEquals(1,res.size());
        foodListLive.observeForever(observer);
        List<FoodEntry> foodList = foodListLive.getValue();

        assertEquals(foodList.get(0).getDone(), 0);
        assertEquals(foodList.get(0).getName(), "cheese");
        assertEquals(foodList.get(0).getExpiringAt(), currentDate);
    }



    @Test
    public void insertFoodEntries_plural_test() throws Exception {
        Date currentDate = new Date();
        FoodEntry food = new FoodEntry(0,"cheese", currentDate);
        FoodEntry food_01 = new FoodEntry(0,"meat", currentDate);

        foodDbDao.insertFoodEntry(food);
        foodDbDao.insertFoodEntry(food_01);

        LiveData<List<FoodEntry>> foodListLive = foodDbDao.loadAllFood();
        Observer<List<FoodEntry>> observer = res -> assertEquals(2,res.size());
        foodListLive.observeForever(observer);
        List<FoodEntry> foodList = foodListLive.getValue();

        // assertEquals(foodList.isEmpty(), false);
        assertEquals(foodList.get(0).getDone(), 0);
        assertEquals(foodList.get(0).getName(), "cheese");
        assertEquals(foodList.get(0).getExpiringAt(), currentDate);

        assertEquals(foodList.get(1).getDone(), 0);
        assertEquals(foodList.get(1).getName(), "meat");
        assertEquals(foodList.get(1).getExpiringAt(), currentDate);

    }


    @Test
    public void updateFoodEntry_test() throws Exception {
        Date currentDate = new Date();
        Date tomorrow    = DateUtility.INSTANCE.addDays(currentDate,1);

        FoodEntry food = new FoodEntry(0,"cheese", currentDate);

        foodDbDao.insertFoodEntry(food);
        foodDbDao.updateFoodEntry(new FoodEntry(0,"meat", tomorrow));

        LiveData<List<FoodEntry>> foodListLive = foodDbDao.loadAllFood();

        Observer<List<FoodEntry>> observer = res -> assertEquals(1,res.size());
        foodListLive.observeForever(observer);
        List<FoodEntry> foodList = foodListLive.getValue();

        assertEquals(foodList.get(0).getDone(), 0);
        assertEquals(foodList.get(0).getName(), "meat");
        assertEquals(foodList.get(0).getExpiringAt(), tomorrow);

    }




    @Test
    public void updateFoodEntries_plural_test() throws Exception {

        Date currentDate = new Date();
        Date tomorrow    = DateUtility.INSTANCE.addDays(currentDate,1);

        FoodEntry food = new FoodEntry(0,"cheese", currentDate);
        FoodEntry food_01 = new FoodEntry(0,"meat", currentDate);

        foodDbDao.insertFoodEntry(food);
        foodDbDao.insertFoodEntry(food_01);
        foodDbDao.updateFoodEntry(new FoodEntry(0,"cheese",tomorrow));
        foodDbDao.updateFoodEntry(new FoodEntry(0,"meat",tomorrow));


        LiveData<List<FoodEntry>> foodListLive = foodDbDao.loadAllFood();
        Observer<List<FoodEntry>> observer = res -> assertEquals(2,res.size());
        foodListLive.observeForever(observer);
        List<FoodEntry> foodList = foodListLive.getValue();

        // assertEquals(foodList.isEmpty(), false);
        assertEquals(foodList.get(0).getDone(), 0);
        assertEquals(foodList.get(0).getName(), "cheese");
        assertEquals(foodList.get(0).getExpiringAt(), tomorrow);

        assertEquals(foodList.get(1).getDone(), 0);
        assertEquals(foodList.get(1).getName(), "meat");
        assertEquals(foodList.get(1).getExpiringAt(), tomorrow);

    }


    @Test
    public void updateDoneField_test() throws Exception {
        Date currentDate = new Date();
        FoodEntry food = new FoodEntry(0,"cheese", currentDate);

        foodDbDao.insertFoodEntry(food);

        LiveData<List<FoodEntry>> foodListLive = foodDbDao.loadAllFood();

        Observer<List<FoodEntry>> observer = res -> assertEquals(1,res.size());
        foodListLive.observeForever(observer);
        List<FoodEntry> foodList = foodListLive.getValue();

        int id = foodList.get(0).getId();
        foodDbDao.updateDoneField(0,id);

        assertEquals(foodList.get(0).getDone(), 1);
        assertEquals(foodList.get(0).getName(), "cheese");
        assertEquals(foodList.get(0).getExpiringAt(), currentDate);
    }

    @Test
    public void updateDoneFields_plural_test() throws Exception {
        Date currentDate = new Date();
        FoodEntry food = new FoodEntry(0,"cheese", currentDate);
        FoodEntry food_01 = new FoodEntry(1,"meat", currentDate);

        foodDbDao.insertFoodEntry(food);
        foodDbDao.insertFoodEntry(food_01);

        LiveData<List<FoodEntry>> foodListLive = foodDbDao.loadAllFood();
        Observer<List<FoodEntry>> observer = res -> assertEquals(2,res.size());
        foodListLive.observeForever(observer);
        List<FoodEntry> foodList = foodListLive.getValue();

        int id = foodList.get(0).getId();
        foodDbDao.updateDoneField(1,id);

        id = foodList.get(1).getId();
        foodDbDao.updateDoneField(0,id);

        // assertEquals(foodList.isEmpty(), false);
        assertEquals(foodList.get(0).getDone(), 1);
        assertEquals(foodList.get(0).getName(), "cheese");
        assertEquals(foodList.get(0).getExpiringAt(), currentDate);

        assertEquals(foodList.get(1).getDone(), 0);
        assertEquals(foodList.get(1).getName(), "meat");
        assertEquals(foodList.get(1).getExpiringAt(), currentDate);
    }




    @Test
    public void loadAllFoodExpiring_test() throws Exception {
        Date currentDate    = new Date();
        Long currDate_long  = DateConverter.INSTANCE.fromDate(currentDate);
        Date tomorrow       = DateUtility.INSTANCE.addDays(currentDate,1);
        FoodEntry food      = new FoodEntry(0,"cheese", currentDate);
        FoodEntry food_01 = new FoodEntry(0,"meat", tomorrow);
        FoodEntry food_02 = new FoodEntry(1,"fruit", currentDate);

        foodDbDao.insertFoodEntry(food);
        foodDbDao.insertFoodEntry(food_01);
        foodDbDao.insertFoodEntry(food_02);

        LiveData<List<FoodEntry>> foodListLive = foodDbDao.loadAllFoodExpiring(currDate_long);
        Observer<List<FoodEntry>> observer = res -> assertEquals(2,res.size());
        foodListLive.observeForever(observer);
        List<FoodEntry> foodList = foodListLive.getValue();

        // assertEquals(foodList.isEmpty(), false);
        assertEquals(foodList.get(0).getDone(), 0);
        assertEquals(foodList.get(0).getName(), "cheese");
        assertEquals(foodList.get(0).getExpiringAt(), currentDate);

        assertEquals(foodList.get(1).getDone(), 0);
        assertEquals(foodList.get(1).getName(), "meat");
        assertEquals(foodList.get(1).getExpiringAt(), tomorrow);

    }

    @Test
    public void loadFoodExpiringToday_test() throws Exception {
        Date currentDate    = new Date();
        Long currDate_long  = DateConverter.INSTANCE.fromDate(currentDate);

        Date yesterday      = DateUtility.INSTANCE.addDays(currentDate,-1);
        Date tomorrow       = DateUtility.INSTANCE.addDays(currentDate,1);
        Long yesterday_long = DateConverter.INSTANCE.fromDate(yesterday);
        Long tomorrow_long  = DateConverter.INSTANCE.fromDate(tomorrow);

        Date next_days      = DateUtility.INSTANCE.addDays(currentDate,3);

        FoodEntry food    = new FoodEntry(0,"cheese", currentDate);
        FoodEntry food_01 = new FoodEntry(0,"meat", tomorrow);
        FoodEntry food_02 = new FoodEntry(1,"fruit", currentDate);
        FoodEntry food_03 = new FoodEntry(0,"salad", next_days);

        foodDbDao.insertFoodEntry(food);
        foodDbDao.insertFoodEntry(food_01);
        foodDbDao.insertFoodEntry(food_02);
        foodDbDao.insertFoodEntry(food_03);

        LiveData<List<FoodEntry>> foodListLive = foodDbDao.loadFoodExpiringToday(yesterday_long,tomorrow_long);
        Observer<List<FoodEntry>> observer = res -> assertEquals(1,res.size());
        foodListLive.observeForever(observer);
        List<FoodEntry> foodList = foodListLive.getValue();

        // assertEquals(foodList.isEmpty(), false);
        assertEquals(foodList.get(0).getDone(), 0);
        assertEquals(foodList.get(0).getName(), "cheese");
        assertEquals(foodList.get(0).getExpiringAt(), currentDate);
    }


    @Test
    public void loadAllFoodDead_test() throws Exception {
        Date currentDate    = new Date();
        Long currDate_long  = DateConverter.INSTANCE.fromDate(currentDate);

        Date tomorrow       = DateUtility.INSTANCE.addDays(currentDate,1);
        Date yesterday      = DateUtility.INSTANCE.addDays(currentDate,-1);
        Date past_days      = DateUtility.INSTANCE.addDays(currentDate,-4);

        FoodEntry food      = new FoodEntry(0,"cheese", currentDate);
        FoodEntry food_01 = new FoodEntry(0,"meat", tomorrow);
        FoodEntry food_02 = new FoodEntry(1,"fruit", yesterday);
        FoodEntry food_03 = new FoodEntry(0,"salad", past_days);
        FoodEntry food_04 = new FoodEntry(0,"fish", yesterday);

        foodDbDao.insertFoodEntry(food);
        foodDbDao.insertFoodEntry(food_01);
        foodDbDao.insertFoodEntry(food_02);
        foodDbDao.insertFoodEntry(food_03);
        foodDbDao.insertFoodEntry(food_04);

        LiveData<List<FoodEntry>> foodListLive = foodDbDao.loadAllFoodDead(currDate_long);
        Observer<List<FoodEntry>> observer = res -> assertEquals(2,res.size());
        foodListLive.observeForever(observer);
        List<FoodEntry> foodList = foodListLive.getValue();

        assertEquals(foodList.get(0).getDone(), 0);
        assertEquals(foodList.get(0).getName(), "salad");
        assertEquals(foodList.get(0).getExpiringAt(), past_days);

        assertEquals(foodList.get(1).getDone(), 0);
        assertEquals(foodList.get(1).getName(), "fish");
        assertEquals(foodList.get(1).getExpiringAt(), yesterday);

    }


    @Test
    public void loadAllFoodSaved_test() throws Exception {
        Date currentDate    = new Date();
        Long currDate_long  = DateConverter.INSTANCE.fromDate(currentDate);

        Date tomorrow       = DateUtility.INSTANCE.addDays(currentDate,1);
        Date yesterday      = DateUtility.INSTANCE.addDays(currentDate,-1);
        Date past_days      = DateUtility.INSTANCE.addDays(currentDate,-4);

        FoodEntry food      = new FoodEntry(1,"cheese", currentDate);
        FoodEntry food_01 = new FoodEntry(0,"meat", tomorrow);
        FoodEntry food_02 = new FoodEntry(1,"fruit", yesterday);
        FoodEntry food_03 = new FoodEntry(1,"salad", past_days);
        FoodEntry food_04 = new FoodEntry(0,"fish", yesterday);

        foodDbDao.insertFoodEntry(food);
        foodDbDao.insertFoodEntry(food_01);
        foodDbDao.insertFoodEntry(food_02);
        foodDbDao.insertFoodEntry(food_03);
        foodDbDao.insertFoodEntry(food_04);

        LiveData<List<FoodEntry>> foodListLive = foodDbDao.loadAllFoodExpiring(currDate_long);
        Observer<List<FoodEntry>> observer = res -> assertEquals(3,res.size());
        foodListLive.observeForever(observer);
        List<FoodEntry> foodList = foodListLive.getValue();

        assertEquals(foodList.get(0).getDone(), 0);
        assertEquals(foodList.get(0).getName(), "cheese");
        assertEquals(foodList.get(0).getExpiringAt(), currentDate);

        assertEquals(foodList.get(1).getDone(), 0);
        assertEquals(foodList.get(1).getName(), "fruit");
        assertEquals(foodList.get(1).getExpiringAt(), yesterday);

        assertEquals(foodList.get(2).getDone(), 0);
        assertEquals(foodList.get(2).getName(), "salad");
        assertEquals(foodList.get(2).getExpiringAt(), past_days);

    }


    @Test
    public void loadFoodById_test() throws Exception {
        Date currentDate = new Date();
        FoodEntry food = new FoodEntry(0,"cheese", currentDate);
        FoodEntry food_01 = new FoodEntry(1,"meat", currentDate);

        foodDbDao.insertFoodEntry(food);
        foodDbDao.insertFoodEntry(food_01);

        LiveData<List<FoodEntry>> foodListLive = foodDbDao.loadAllFood();
        Observer<List<FoodEntry>> observer = res -> assertEquals(2,res.size());
        foodListLive.observeForever(observer);
        List<FoodEntry> foodList = foodListLive.getValue();

        int id = foodList.get(0).getId();
        foodDbDao.updateDoneField(1,id);

        id = foodList.get(1).getId();
        foodDbDao.updateDoneField(0,id);

        // assertEquals(foodList.isEmpty(), false);
        assertEquals(foodList.get(0).getDone(), 1);
        assertEquals(foodList.get(0).getName(), "cheese");
        assertEquals(foodList.get(0).getExpiringAt(), currentDate);

        assertEquals(foodList.get(1).getDone(), 0);
        assertEquals(foodList.get(1).getName(), "meat");
        assertEquals(foodList.get(1).getExpiringAt(), currentDate);
    }










}
