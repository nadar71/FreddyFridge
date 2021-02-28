package eu.indiewalkabout.fridgemanager.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import eu.indiewalkabout.fridgemanager.data.db.DateConverter;
import eu.indiewalkabout.fridgemanager.data.db.FoodDatabase;
import eu.indiewalkabout.fridgemanager.data.db.FoodDbDao;
import eu.indiewalkabout.fridgemanager.data.model.FoodEntry;
import eu.indiewalkabout.fridgemanager.util.DateUtility;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/* TODO : fix error
FridgeManagerRepository_test  bug #15
Launching every single tests by itself they are passed, but running altogether only the first
it's ok, the other no, independently by which tests are active in the class.

Tried with reset mocks in teardown function and remock explicitly inside @before method,
as well as doing it inside each test (both reset and remock), check the commented code.
 */

@RunWith(MockitoJUnitRunner.class)
public class FridgeManagerRepository_test {

    @Mock
    FoodDatabase mockDb;

    @Mock
    FoodDbDao mockDao;

    @Mock
    FoodEntry foodEntry;

    FridgeManagerRepository repository;


    @Before
    public void setup() {
        // mockDb = mock(FoodDatabase.class);
        // mockDao = mock(FoodDbDao.class);
        // gameResult = mock(FoodEntry.class);
        MockitoAnnotations.initMocks(this);
        repository = FridgeManagerRepository.Companion.getInstance(mockDb);
        when(mockDb.foodDbDao()).thenReturn(mockDao);

    }

/*
    @After
    public void teardown() {
        Mockito.reset(mockDb);
        Mockito.reset(mockDao);
        Mockito.reset(foodEntry);
        // Mockito.clearInvocations();
        Mockito.validateMockitoUsage();
        // mockDb = null;
        // mockDao = null;
        // foodEntry = null;
        // repository = null;
    }

 */





    @Test // OK runned alone
    public void loadAllFood_test(){
        repository.loadAllFood();
        verify(mockDao,times(1)).loadAllFood();
    }

    @Test // OK runned alone
    public void loadAllFoodExpiring_test(){
        Date today          = new Date();
        Long today_long     = DateConverter.INSTANCE.fromDate(today);

        repository.loadAllFoodExpiring(today_long);
        verify(mockDao,times(1)).loadAllFoodExpiring(today_long );
    }



    @Test // OK runned alone
    public void loadFoodExpiringToday_test(){
        Date today          = new Date();
        Date yesterday      = DateUtility.INSTANCE.addDays(today,-1);
        Date tomorrow       = DateUtility.INSTANCE.addDays(today,1);

        Long yesterday_long = DateConverter.INSTANCE.fromDate(yesterday);
        Long tomorrow_long  = DateConverter.INSTANCE.fromDate(tomorrow);

        repository.loadFoodExpiringToday(yesterday_long,tomorrow_long);
        verify(mockDao,times(1)).loadFoodExpiringToday(yesterday_long,tomorrow_long );
    }


    @Test // OK runned alone
    public void loadAllFoodDead_test(){
        Date today          = new Date();
        Long today_long     = DateConverter.INSTANCE.fromDate(today);

        repository.loadAllFoodDead(today_long);
        verify(mockDao,times(1)).loadAllFoodDead(today_long );
    }

    @Test // OK runned alone
    public void loadAllFoodSaved_test(){
        repository.loadAllFoodSaved();
        verify(mockDao,times(1)).loadAllFoodSaved( );
    }


    @Test // OK runned alone
    public void loadFoodById_test(){
        repository.loadFoodById(1);
        verify(mockDao,times(1)).loadFoodById(1);
    }


    @Test // OK runned alone
    public void insertFoodEntry_test(){
        Date today     = new Date();
        Date next_days = DateUtility.INSTANCE.addDays(today,4);

        FoodEntry foodEntry    = new FoodEntry(0,"ham",today);
        FoodEntry foodEntry_01 = new FoodEntry(0,"cheese",next_days);

        repository.insertFoodEntry(foodEntry);
        repository.insertFoodEntry(foodEntry_01);

        verify(mockDao,times(2)).insertFoodEntry(any(FoodEntry.class));
    }


    @Test // OK runned alone
    public void updateFoodEntry_test(){
        Date today     = new Date();
        Date next_days = DateUtility.INSTANCE.addDays(today,4);

        FoodEntry foodEntry    = new FoodEntry(0,"ham",today);
        FoodEntry foodEntry_01 = new FoodEntry(0,"ham",next_days);

        repository.insertFoodEntry(foodEntry);
        repository.updateFoodEntry(foodEntry_01);

        verify(mockDao,times(1)).insertFoodEntry(any(FoodEntry.class));
        verify(mockDao,times(1)).updateFoodEntry(any(FoodEntry.class));
    }


    @Test // OK runned alone
    public void updateDoneField_test(){
        Date today     = new Date();

        FoodEntry foodEntry    = new FoodEntry(0,"ham",today);

        repository.insertFoodEntry(foodEntry);
        repository.updateDoneField(1,1);

        verify(mockDao,times(1)).insertFoodEntry(any(FoodEntry.class));
        verify(mockDao,times(1)).updateDoneField(1,1);

    }


    @Test // OK runned alone
    public void deleteFoodEntry_test(){
        Date today     = new Date();

        FoodEntry foodEntry    = new FoodEntry(0,"ham",today);

        repository.insertFoodEntry(foodEntry);
        repository.deleteFoodEntry(foodEntry);

        verify(mockDao,times(1)).insertFoodEntry(any(FoodEntry.class));
        verify(mockDao,times(1)).deleteFoodEntry(any(FoodEntry.class));

    }



}