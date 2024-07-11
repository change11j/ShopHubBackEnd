package org.ctc.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static Date getStartOfWeek(){
        LocalDate startOfWeek=LocalDate.now().with(DayOfWeek.MONDAY);
        return Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date getEndOfWeek(){
        LocalDate endOfWeek=LocalDate.now().with(DayOfWeek.SUNDAY);
        return Date.from(endOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date getStartOfMonth(){
        LocalDate startOfMonth=LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        return Date.from(startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    public static Date getEndOfMonth(){
        LocalDate endOfMonth=LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return Date.from(endOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date getMinDate(){
        Calendar cal=Calendar.getInstance();
        cal.set(1900,Calendar.JANUARY,1,0,0,0);
        cal.set(Calendar.MILLISECOND,0);
        return cal.getTime();
    }

    public static Date getMaxDate(){
        Calendar cal=Calendar.getInstance();
        cal.set(9999,Calendar.DECEMBER,31,23,59,59);
        cal.set(Calendar.MILLISECOND,999);
        return cal.getTime();
    }

}
