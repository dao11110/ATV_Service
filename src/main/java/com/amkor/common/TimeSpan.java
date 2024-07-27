package com.amkor.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Data
public class TimeSpan {
    private static final List<String> DAY_SHIFT_TIMES = Arrays.asList(
            "07:30 - 08:30", "08:30 - 09:30", "09:30 - 10:30", "10:30 - 11:30", "11:30 - 12:30", "12:30 - 13:30",
            "13:30 - 14:30", "14:30 - 15:30", "15:30 - 16:30", "16:30 - 17:30", "17:30 - 18:30", "18:30 - 19:30"
    );

    private static final List<String> NIGHT_SHIFT_TIMES = Arrays.asList(
            "19:30 - 20:30", "20:30 - 21:30", "21:30 - 22:30", "22:30 - 23:30", "23:30 - 00:30", "00:30 - 01:30",
            "01:30 - 02:30", "02:30 - 03:30", "03:30 - 04:30", "04:30 - 05:30", "05:30 - 06:30", "06:30 - 07:30"
    );

    private Type type;

    private ShiftType shiftType;

    private Date startDate;

    private Date endDate;

    public static TimeSpan of(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }

        TimeSpan instance = new TimeSpan();

        long milliseconds = endDate.getTime() - startDate.getTime();
        if (milliseconds == 3600000) {
            instance.setType(Type.HOURLY);
        } else if (milliseconds == 12 * 3600000) {
            instance.setType(Type.DAILY);
        } else {
            instance.setType(Type.FULL);
        }

        instance.setShiftType(TimeSpan.shiftTypeFrom(startDate, endDate));
        instance.setStartDate(startDate);
        instance.setEndDate(endDate);
        return instance;
    }

    public static TimeSpan of(Type type, ShiftType shiftType, Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }

        TimeSpan instance = new TimeSpan();
        instance.setType(type);
        instance.setShiftType(shiftType);
        instance.setStartDate(startDate);
        instance.setEndDate(endDate);
        return instance;
    }

    public static TimeSpan now(Type type) {
        return TimeSpan.from(Calendar.getInstance(), type);
    }

    public static TimeSpan from(Calendar cal, Type type) {
        Calendar calendar = (Calendar) cal.clone();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 30);

        Calendar start = (Calendar) calendar.clone();
        Calendar end = (Calendar) calendar.clone();

        calendar.add(Calendar.MILLISECOND, -1);

        calendar.set(Calendar.HOUR_OF_DAY, 7);
        Calendar day = (Calendar) calendar.clone();

        calendar.set(Calendar.HOUR_OF_DAY, 19);
        Calendar night = (Calendar) calendar.clone();

        if (type == Type.DAILY) {
            if (cal.before(day)) {
                night.add(Calendar.DAY_OF_YEAR, -1);
                return TimeSpan.of(type, ShiftType.NIGHT, night.getTime(), day.getTime());
            } else if (night.after(cal)) {
                return TimeSpan.of(type, ShiftType.DAY, day.getTime(), night.getTime());
            } else {
                day.add(Calendar.DAY_OF_YEAR, 1);
                return TimeSpan.of(type, ShiftType.NIGHT, night.getTime(), day.getTime());
            }
        } else if (type == Type.HOURLY) {
            ShiftType shiftType;
            if (cal.before(day)) {
                shiftType = ShiftType.NIGHT;
            } else if (night.after(cal)) {
                shiftType = ShiftType.DAY;
            } else {
                shiftType = ShiftType.NIGHT;
            }

            if (cal.before(start)) {
                start.add(Calendar.HOUR_OF_DAY, -1);
                return TimeSpan.of(type, shiftType, start.getTime(), end.getTime());
            } else {
                end.add(Calendar.HOUR_OF_DAY, 1);
                return TimeSpan.of(type, shiftType, start.getTime(), end.getTime());
            }
        } else {
            if (cal.before(day)) {
                night = (Calendar) day.clone();
                night.add(Calendar.DAY_OF_YEAR, -1);
                return TimeSpan.of(type, ShiftType.NIGHT, night.getTime(), day.getTime());
            } else {
                night = (Calendar) day.clone();
                night.add(Calendar.DAY_OF_YEAR, 1);
                return TimeSpan.of(type, null, day.getTime(), night.getTime());
            }
        }
    }

    public static TimeSpan from(String timeSpan, TimeSpan defaultValue) {
        TimeSpan fullTimeSpan = TimeSpan.from(timeSpan);
        if (fullTimeSpan == null) {
            return defaultValue;
        }
        return fullTimeSpan;
    }

    public static TimeSpan from(String timeSpan) {
        SimpleDateFormat FULL_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        if (StringUtils.isEmpty(timeSpan)) {
            return null;
        }

        String[] dates = timeSpan.split(" - ");
        if (dates.length != 2) {
            return null;
        }

        try {
            Calendar calendar = Calendar.getInstance();
            Date startDate = FULL_FORMAT.parse(dates[0]);
            calendar.setTime(startDate);
            calendar.add(Calendar.SECOND, -1);
            startDate = calendar.getTime();

            Date endDate = FULL_FORMAT.parse(dates[1]);
            calendar.setTime(endDate);
            calendar.add(Calendar.SECOND, -1);
            endDate = calendar.getTime();

            return TimeSpan.of(startDate, endDate);
        } catch (Exception e) {
            log.error("### from parse {}, {}, {} error", timeSpan, dates[0], dates[1], e);
            return null;
        }
    }

    public static TimeSpan from(String timeSpan, Type type) {
        SimpleDateFormat DAILY_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
        if (type == Type.DAILY) {
            String[] args = timeSpan.split(" ");
            if (args.length != 2) {
                return null;
            }
            try {
                Date startDate = DAILY_FORMAT.parse(args[0]);
                String shiftType = args[1];

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                calendar.clear(Calendar.MILLISECOND);
                calendar.clear(Calendar.SECOND);
                calendar.set(Calendar.MINUTE, 30);
                if ("DAY".equalsIgnoreCase(shiftType)) {
                    calendar.set(Calendar.HOUR_OF_DAY, 7);
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, 19);
                }

                return TimeSpan.from(calendar, type);
            } catch (ParseException e) {
                log.error("### from {} {}", timeSpan, type, e);
            }
        }
        return null;
    }

    public static String format(TimeSpan timeSpan, Type typeFormat) {
        SimpleDateFormat HOURLY_FORMAT = new SimpleDateFormat("HH:mm");
        SimpleDateFormat DAILY_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat FULL_FORMAT = new SimpleDateFormat("MM/dd HH:mm");

        if (timeSpan == null || timeSpan.getStartDate() == null || timeSpan.getEndDate() == null) {
            return "";
        }

        if (typeFormat == Type.HOURLY) {
            return HOURLY_FORMAT.format(timeSpan.getStartDate()) + " - " + HOURLY_FORMAT.format(timeSpan.getEndDate());
        } else if (typeFormat == Type.DAILY) {
            if (timeSpan.getType() == Type.DAILY) {
                return DAILY_FORMAT.format(timeSpan.getStartDate()) + " " + timeSpan.getShiftType();
            } else if (timeSpan.getType() == Type.HOURLY) {
                if (timeSpan.getShiftType() == ShiftType.DAY) {
                    return DAILY_FORMAT.format(timeSpan.getStartDate()) + " " + timeSpan.getShiftType();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(timeSpan.getStartDate());
                if (calendar.get(Calendar.HOUR_OF_DAY) <= 7) {
                    calendar.add(Calendar.DAY_OF_YEAR, -1);
                }
                return DAILY_FORMAT.format(calendar.getTime()) + " " + ShiftType.NIGHT;
            }
            return "";
        }
        return FULL_FORMAT.format(timeSpan.getStartDate()) + " - " + FULL_FORMAT.format(timeSpan.getEndDate());
    }

    public static ShiftType shiftTypeFrom(Date startDate, Date endDate) {

        Calendar cal = Calendar.getInstance();

        cal.setTime(startDate);
        Calendar start = (Calendar) cal.clone();
        Calendar calendar = (Calendar) cal.clone();

        cal.setTime(endDate);
        Calendar end = (Calendar) cal.clone();

        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 30);
        calendar.add(Calendar.MILLISECOND, -1);

        calendar.set(Calendar.HOUR_OF_DAY, 7);
        Calendar day = (Calendar) calendar.clone();

        calendar.set(Calendar.HOUR_OF_DAY, 19);
        Calendar night = (Calendar) calendar.clone();

        // full
        if (end.getTimeInMillis() - start.getTimeInMillis() > 12 * 60 * 60 * 1000) {
            return start.after(day) ? ShiftType.NIGHT : ShiftType.DAY;
        }

        // daily
        if (end.getTimeInMillis() - start.getTimeInMillis() > 60 * 60 * 1000) {
            return start.after(day) ? ShiftType.NIGHT : ShiftType.DAY;
        }

        // hourly
        if (start.before(day)) {
            return ShiftType.NIGHT;
        } else if (night.after(start)) {
            return ShiftType.DAY;
        } else {
            return ShiftType.NIGHT;
        }
    }

    public static int getShiftIndex(String shift) {
        if (DAY_SHIFT_TIMES.indexOf(shift) != -1) {
            return DAY_SHIFT_TIMES.indexOf(shift);
        }

        if (NIGHT_SHIFT_TIMES.indexOf(shift) != -1) {
            return NIGHT_SHIFT_TIMES.indexOf(shift);
        }

        return -1;
    }

    public boolean isNow() {
        Date now = new Date();
        return startDate.before(now) && endDate.after(now);
    }

    public int getShiftIndex() {
        if (shiftType == ShiftType.DAY) {
            return DAY_SHIFT_TIMES.indexOf(toString());
        }

        if (shiftType == ShiftType.NIGHT) {
            return NIGHT_SHIFT_TIMES.indexOf(toString());
        }

        return -1;
    }

    @Override
    public String toString() {
        SimpleDateFormat HOURLY_FORMAT = new SimpleDateFormat("HH:mm");
        SimpleDateFormat DAILY_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat FULL_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        if (startDate == null || endDate == null) {
            return "";
        }

        if (type == Type.HOURLY) {
            return HOURLY_FORMAT.format(startDate) + " - " + HOURLY_FORMAT.format(endDate);
        } else if (type == Type.DAILY) {
            return DAILY_FORMAT.format(startDate) + " " + shiftType;
        }
        return FULL_FORMAT.format(startDate) + " - " + FULL_FORMAT.format(endDate);
    }

    public enum Type {
        HOURLY,
        DAILY,
        FULL
    }
}
