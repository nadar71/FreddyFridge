package eu.indiewalkabout.fridgemanager.db_test;


import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.io.IOException;

import eu.indiewalkabout.fridgemanager.data.FoodDatabase;
import eu.indiewalkabout.fridgemanager.data.FoodDbDao;

@RunWith(AndroidJUnit4.class)
public class FoodDbDao_test {

    @Rule
    // public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private FoodDatabase foodDatabase;
    private FoodDbDao foodDbDao;


    @Before
    public void createDb() {
        foodDatabase = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().getContext(),
                FoodDatabase.class).build();
        foodDbDao = foodDatabase.foodDbDao();

    }

    @After
    public void closeDb() throws IOException {

        foodDatabase.close();
    }



}
