package com.foxconn.fii.common;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeSpanTest {

//    @Test
//    public void now() {
//        TimeSpan timeSpan = TimeSpan.now(TimeSpan.Type.FULL);
//        assertThat(timeSpan).isNotNull();
//    }
//
//    @Test
//    public void fromDailyTest() throws ParseException {
//        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
//
//        // DAILY 2019/01/23 09:15:00
//        Date now = df.parse("2019/01/23 09:15:00");
//        calendar.setTime(now);
//
//        TimeSpan timeSpan = TimeSpan.from(calendar, TimeSpan.Type.DAILY);
//
//        assertThat(timeSpan).isNotNull();
//        assertThat(timeSpan.getShiftType()).isEqualTo(ShiftType.DAY);
//        assertThat(df.format(timeSpan.getStartDate())).isEqualTo("2019/01/23 07:29:59");
//        assertThat(df.format(timeSpan.getEndDate())).isEqualTo("2019/01/23 19:29:59");
//
//        // DAILY 2019/01/23 06:15:00
//        now = df.parse("2019/01/23 06:15:00");
//        calendar.setTime(now);
//
//        timeSpan = TimeSpan.from(calendar, TimeSpan.Type.DAILY);
//
//        assertThat(timeSpan).isNotNull();
//        assertThat(timeSpan.getShiftType()).isEqualTo(ShiftType.NIGHT);
//        assertThat(df.format(timeSpan.getStartDate())).isEqualTo("2019/01/22 19:29:59");
//        assertThat(df.format(timeSpan.getEndDate())).isEqualTo("2019/01/23 07:29:59");
//
//        // DAILY 2019/01/23 21:15:00
//        now = df.parse("2019/01/23 21:15:00");
//        calendar.setTime(now);
//
//        timeSpan = TimeSpan.from(calendar, TimeSpan.Type.DAILY);
//
//        assertThat(timeSpan).isNotNull();
//        assertThat(timeSpan.getShiftType()).isEqualTo(ShiftType.NIGHT);
//        assertThat(df.format(timeSpan.getStartDate())).isEqualTo("2019/01/23 19:29:59");
//        assertThat(df.format(timeSpan.getEndDate())).isEqualTo("2019/01/24 07:29:59");
//
//    }
//
//    @Test
//    public void fromHourlyTest() throws ParseException {
//        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
//
//        // HOURLY 2019/01/23 09:45:00
//        Date now = df.parse("2019/01/23 09:45:00");
//        calendar.setTime(now);
//
//        TimeSpan timeSpan = TimeSpan.from(calendar, TimeSpan.Type.HOURLY);
//
//        assertThat(timeSpan).isNotNull();
//        assertThat(timeSpan.getShiftType()).isEqualTo(ShiftType.DAY);
//        assertThat(df.format(timeSpan.getStartDate())).isEqualTo("2019/01/23 09:30:00");
//        assertThat(df.format(timeSpan.getEndDate())).isEqualTo("2019/01/23 10:30:00");
//
//        // HOURLY 2019/01/23 09:30:00
//        now = df.parse("2019/01/23 09:30:00");
//        calendar.setTime(now);
//
//        timeSpan = TimeSpan.from(calendar, TimeSpan.Type.HOURLY);
//
//        assertThat(timeSpan).isNotNull();
//        assertThat(timeSpan.getShiftType()).isEqualTo(ShiftType.DAY);
//        assertThat(df.format(timeSpan.getStartDate())).isEqualTo("2019/01/23 09:30:00");
//        assertThat(df.format(timeSpan.getEndDate())).isEqualTo("2019/01/23 10:30:00");
//
//        // HOURLY 2019/01/23 06:15:00
//        now = df.parse("2019/01/23 06:15:00");
//        calendar.setTime(now);
//
//        timeSpan = TimeSpan.from(calendar, TimeSpan.Type.HOURLY);
//
//        assertThat(timeSpan).isNotNull();
//        assertThat(timeSpan.getShiftType()).isEqualTo(ShiftType.NIGHT);
//        assertThat(df.format(timeSpan.getStartDate())).isEqualTo("2019/01/23 05:30:00");
//        assertThat(df.format(timeSpan.getEndDate())).isEqualTo("2019/01/23 06:30:00");
//    }
//
//    @Test
//    public void fromStringTest() {
//        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        String timeSpanString = "2019/01/18 07:30 - 2019/01/25 07:30";
//
//        TimeSpan timeSpan = TimeSpan.from(timeSpanString);
//        assertThat(timeSpan).isNotNull();
//        assertThat(timeSpan.getShiftType()).isEqualTo(ShiftType.DAY);
//        assertThat(df.format(timeSpan.getStartDate())).isEqualTo("2019/01/23 07:30:00");
//        assertThat(df.format(timeSpan.getEndDate())).isEqualTo("2019/01/23 19:30:00");
//    }
//
//    @Test
//    public void shiftTypeFromTest() throws ParseException {
//        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//
//        // HOURLY 06:30 - 07:30
//        Date start = df.parse("2019/01/23 06:30:00");
//        Date end = df.parse("2019/01/23 07:30:00");
//
//        ShiftType shiftType = TimeSpan.shiftTypeFrom(start, end);
//        assertThat(shiftType).isEqualTo(ShiftType.NIGHT);
//
//        // HOURLY 07:30 - 08:30
//        start = df.parse("2019/01/23 07:30:00");
//        end = df.parse("2019/01/23 08:30:00");
//
//        shiftType = TimeSpan.shiftTypeFrom(start, end);
//        assertThat(shiftType).isEqualTo(ShiftType.DAY);
//
//        // HOURLY 09:30 - 10:30
//        start = df.parse("2019/01/23 09:30:00");
//        end = df.parse("2019/01/23 10:30:00");
//
//        shiftType = TimeSpan.shiftTypeFrom(start, end);
//        assertThat(shiftType).isEqualTo(ShiftType.DAY);
//
//        // HOURLY 19:30 - 20:30
//        start = df.parse("2019/01/23 19:30:00");
//        end = df.parse("2019/01/23 20:30:00");
//
//        shiftType = TimeSpan.shiftTypeFrom(start, end);
//        assertThat(shiftType).isEqualTo(ShiftType.NIGHT);
//
//        // HOURLY 21:30 - 22:30
//        start = df.parse("2019/01/23 21:30:00");
//        end = df.parse("2019/01/23 22:30:00");
//
//        shiftType = TimeSpan.shiftTypeFrom(start, end);
//        assertThat(shiftType).isEqualTo(ShiftType.NIGHT);
//    }
//
//    @Test
//    public void shiftTypeFromDailyTest() throws ParseException {
//        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
//
//        // DAILY 07:30 - 19:30
//        Date start = df.parse("2019/01/23 07:29:59.999");
//        Date end = df.parse("2019/01/23 19:29:59.999");
//
//        ShiftType shiftType = TimeSpan.shiftTypeFrom(start, end);
//        assertThat(shiftType).isEqualTo(ShiftType.DAY);
//
//        // DAILY 19:30 - 07:30
//        start = df.parse("2019/01/23 19:29:59.999");
//        end = df.parse("2019/01/24 07:29:59.999");
//
//        shiftType = TimeSpan.shiftTypeFrom(start, end);
//        assertThat(shiftType).isEqualTo(ShiftType.NIGHT);
//    }
//
//    @Test
//    public void toStringFormatDailyTest() throws ParseException {
//        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
//
//        // HOURLY
//        Date start = df.parse("2019/01/23 07:30:00.000");
//        Date end = df.parse("2019/01/23 08:30:00.000");
//
//        TimeSpan timeSpan = TimeSpan.of(start, end);
//        assertThat(TimeSpan.format(timeSpan, TimeSpan.Type.DAILY)).isEqualTo("2019/01/23 DAY");
//
//        // HOURLY
//        start = df.parse("2019/01/23 20:30:00.000");
//        end = df.parse("2019/01/23 21:30:00.000");
//
//        timeSpan = TimeSpan.of(start, end);
//        assertThat(TimeSpan.format(timeSpan, TimeSpan.Type.DAILY)).isEqualTo("2019/01/23 NIGHT");
//
//        // HOURLY
//        start = df.parse("2019/01/24 03:30:00.000");
//        end = df.parse("2019/01/24 04:30:00.000");
//
//        timeSpan = TimeSpan.of(start, end);
//        assertThat(TimeSpan.format(timeSpan, TimeSpan.Type.DAILY)).isEqualTo("2019/01/23 NIGHT");
//    }
}
