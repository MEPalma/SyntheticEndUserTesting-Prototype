package common.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private static final DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    private static final DateFormat timeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static String formatEpochDate(int epochDate) {
        return dateFormatter.format(new Date(epochDate * 1_000L));
    }

    public static String formatEpochTime(long epochTime) {
        return timeFormatter.format(new Date(epochTime));
    }

    public static int getEpochDateNow() {
        return LocalDateTime.now().toLocalTime().toSecondOfDay();
    }

    public static long getEpochTimeNowMinusDays(int days) {
        long ns = (long) days * 86_4000 * 1_0000 * 1_0000;
        return getEpochDateNow() - ns;
    }

    public static long getEpochTimeNow() {
        return Calendar.getInstance().getTimeInMillis();
    }
}
