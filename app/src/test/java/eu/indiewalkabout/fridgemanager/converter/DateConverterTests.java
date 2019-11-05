package eu.indiewalkabout.fridgemanager.converter;

import org.junit.Test;

import eu.indiewalkabout.fridgemanager.data.DateConverter;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;


public class DateConverterTests {
    @Test
    public void toDate_test_custom() {
        assertNull(DateConverter.INSTANCE.toDate(null));

        Long date_01_long = 1537868854000L; // Tuesday, 25 Sep 2018 11:47:34 GMT
        Date date_01      = DateConverter.INSTANCE.toDate(date_01_long);

        System.out.println("Date : "+date_01);
        Calendar cal      = Calendar.getInstance();
        cal.setTime(date_01);
        System.out.println(cal);


        assertEquals(3,cal.get(Calendar.DAY_OF_WEEK));
        System.out.println("Calendar.DAY_OF_WEEK : "+Calendar.DAY_OF_WEEK);

        assertEquals(25,cal.get(Calendar.DAY_OF_MONTH));
        System.out.println("Calendar.DAY_OF_MONTH : "+Calendar.DAY_OF_MONTH);

        assertEquals(8,cal.get(Calendar.MONTH));
        System.out.println("Calendar.MONTH : "+Calendar.MONTH);

        assertEquals(2018,cal.get(Calendar.YEAR));
        System.out.println("Calendar.YEAR : "+Calendar.YEAR);

        assertEquals(11,cal.get(Calendar.HOUR));
        System.out.println("Calendar.HOUR : "+Calendar.HOUR);

        assertEquals(47,cal.get(Calendar.MINUTE));
        System.out.println("Calendar.MINUTE : "+Calendar.MINUTE);

        assertEquals(34,cal.get(Calendar.SECOND));
        System.out.println("Calendar.SECOND : "+Calendar.SECOND);

    }

    @Test
    public void toDate_test_today() {
        assertNull(DateConverter.INSTANCE.fromDate(null));

        Date today = new Date();
        Calendar today_cal = Calendar.getInstance();
        today_cal.setTime(today);
        System.out.println("today_cal : "+today_cal.getTimeInMillis());

        // cinvert from long to date
        Date test_date = DateConverter.INSTANCE.toDate(today_cal.getTimeInMillis());

        Calendar test_cal = Calendar.getInstance();
        test_cal.setTime(test_date);


        // check if equals
        assertEquals(today_cal.get(Calendar.DAY_OF_WEEK),test_cal.get(Calendar.DAY_OF_WEEK));
        assertEquals(today_cal.get(Calendar.DAY_OF_MONTH),test_cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(today_cal.get(Calendar.MONTH),test_cal.get(Calendar.MONTH));
        assertEquals(today_cal.get(Calendar.YEAR),test_cal.get(Calendar.YEAR));
        assertEquals(today_cal.get(Calendar.HOUR),test_cal.get(Calendar.HOUR));
        assertEquals(today_cal.get(Calendar.MINUTE),test_cal.get(Calendar.MINUTE));
        assertEquals(today_cal.get(Calendar.SECOND),test_cal.get(Calendar.SECOND));

    }



    @Test
    public void fromDate_test() {
        assertNull(DateConverter.INSTANCE.fromDate(null));

        Date today = new Date();
        Calendar today_cal = Calendar.getInstance();
        today_cal.setTime(today);

        // convert current to long
        Long date_long =  DateConverter.INSTANCE.fromDate(today);

        Date test_date = new Date();
        test_date.setTime(date_long);

        Calendar test_cal = Calendar.getInstance();
        test_cal.setTime(test_date);

        // check if equals
        assertEquals(today_cal.get(Calendar.DAY_OF_WEEK),test_cal.get(Calendar.DAY_OF_WEEK));
        assertEquals(today_cal.get(Calendar.DAY_OF_MONTH),test_cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(today_cal.get(Calendar.MONTH),test_cal.get(Calendar.MONTH));
        assertEquals(today_cal.get(Calendar.YEAR),test_cal.get(Calendar.YEAR));
        assertEquals(today_cal.get(Calendar.HOUR),test_cal.get(Calendar.HOUR));
        assertEquals(today_cal.get(Calendar.MINUTE),test_cal.get(Calendar.MINUTE));
        assertEquals(today_cal.get(Calendar.SECOND),test_cal.get(Calendar.SECOND));

    }


}