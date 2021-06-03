package eu.claymc.proxysystem.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class TimeUtil {

    public enum TimeUnit {
        FIT,
        DAYS,
        HOURS,
        MINUTES,
        SECONDS,
        MILLISECONDS
    }

    public static String convertString(long time) {
        return convertString(time, 1, TimeUnit.FIT);
    }

    public static String convertString(long time, int trim) {
        return convertString(Math.max(0, time), trim, TimeUnit.FIT);
    }

    public static String convertString(long time, int trim, TimeUnit type) {
        if (time == -1) return "Permanent";

        if (type == TimeUnit.FIT) {
            if (time < 60000) {
                type = TimeUnit.SECONDS;
            } else if (time < 3600000) {
                type = TimeUnit.MINUTES;
            } else if (time < 86400000) {
                type = TimeUnit.HOURS;
            } else {
                type = TimeUnit.DAYS;
            }
        }

        String text;
        double num;
        if (trim == 0) {
            if (type == TimeUnit.DAYS) {
                text = (num = trim(trim, time / 86400000d)) + " Day";
            } else if (type == TimeUnit.HOURS) {
                text = (num = trim(trim, time / 3600000d)) + " Hour";
            } else if (type == TimeUnit.MINUTES) {
                text = (num = trim(trim, time / 60000d)) + " Minute";
            } else if (type == TimeUnit.SECONDS) {
                text = (int) (num = (int) trim(trim, time / 1000d)) + " Second";
            } else {
                text = (int) (num = (int) trim(trim, time)) + " Millisecond";
            }
        } else {
            if (type == TimeUnit.DAYS) {
                text = (num = trim(trim, time / 86400000d)) + " Day";
            } else if (type == TimeUnit.HOURS) {
                text = (num = trim(trim, time / 3600000d)) + " Hour";
            } else if (type == TimeUnit.MINUTES) {
                text = (num = trim(trim, time / 60000d)) + " Minute";
            } else if (type == TimeUnit.SECONDS) {
                text = (num = trim(trim, time / 1000d)) + " Second";
            } else {
                text = (int) (num = (int) trim(0, time)) + " Millisecond";
            }
        }

        if (num != 1)
            text += "s";

        return text;
    }

    private static double trim(int degree, double d) {
        String format = "#.#";

        for (int i = 1; i < degree; i++)
            format += "#";

        DecimalFormatSymbols symb = new DecimalFormatSymbols(Locale.US);
        DecimalFormat twoDForm = new DecimalFormat(format, symb);
        return Double.valueOf(twoDForm.format(d));
    }

}
