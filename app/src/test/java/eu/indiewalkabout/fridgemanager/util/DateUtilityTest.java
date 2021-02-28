package eu.indiewalkabout.fridgemanager.util;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import eu.indiewalkabout.fridgemanager.data.db.DateConverter;

public class DateUtilityTest {
    
    @Test
    public void  getDAY_IN_MILLIS_test() {
        long days_millis = DateUtility.INSTANCE.getDAY_IN_MILLIS();
        Assert.assertEquals("86400000",Long.toString(days_millis));
    }


    /* Not testable apart by itself without a parameter
    @Test
    public void  getNormalizedUtcMsForToday_test() {
        long normalizedUtc = DateUtility.INSTANCE.getNormalizedUtcMsForToday();
    }

    @Test
    public void  getNormalizedUtcDateForToday_test() {
    }

    @Test
    public void  getLocalMidnightFromNormalizedUtcDate_test() {
    }

    */



    @Test  // KO, must verify
    public void  addDays_test() {
        Date today = new Date();

        Date tomorrow = DateUtility.INSTANCE.addDays(today,1);
        long tomorrow_long = DateConverter.INSTANCE.fromDate(tomorrow);

        LocalDateTime today_check = LocalDateTime.now();
        LocalDateTime tomorrow_check = today_check.plusDays(1);

        long tomorrow_check_long = tomorrow_check.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();
        Assert.assertEquals(tomorrow_long,tomorrow_check_long);
    }
}
