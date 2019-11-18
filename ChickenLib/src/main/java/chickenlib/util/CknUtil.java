package chickenlib.util;

import android.icu.text.SimpleDateFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Locale;

public class CknUtil {

    /**
     * This method returns the current time in seconds with nano-second precision.
     *
     * @return current time in seconds.
     */
    public static double getCurrentTime()
    {
        return System.nanoTime() / 1000000000.0;
    }   //getCurrentTime

    /**
     * This method returns the current time in msec.
     *
     * @return current time in msec.
     */
    public static long getCurrentTimeMillis()
    {
        return System.currentTimeMillis();
    }   //getCurrentTimeMillis

    /**
     * This method returns the current time in nano second.
     *
     * @return current time in nano second.
     */
    public static long getCurrentTimeNanos()
    {
        return System.nanoTime();
    }   //getCurrentTimeNanos

    /**
     * This method returns the current time stamp with the specified format.
     *
     * @param format specifies the time stamp format.
     * @return current time stamp string with the specified format.
     */
    public static String getTimestamp(String format)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        return dateFormat.format(new Date());
    }   //getTimestamp

    /**
     * This method returns the current time stamp with the default format.
     *
     * @return current time stamp string with the default format.
     */
    public static String getTimestamp()
    {
        return getTimestamp("yyyyMMdd@HHmmss");
    }   //getTimestamp

    /**
     * This method clips the given value to the range limited by the given low and high limits.
     *
     * @param value     specifies the value to be clipped
     * @param lowLimit  specifies the low limit of the range.
     * @param highLimit specifies the high limit of the range.
     * @return the result of the clipped value.
     */
    public static int clipRange(int value, int lowLimit, int highLimit)
    {
        return Math.min(Math.max(value, lowLimit), highLimit);
    }   //clipRange

    /**
     * This method clips the given value to the range limited by the given low and high limits.
     *
     * @param value     specifies the value to be clipped
     * @param lowLimit  specifies the low limit of the range.
     * @param highLimit specifies the high limit of the range.
     * @return the result of the clipped value.
     */
    public static double clipRange(double value, double lowLimit, double highLimit)
    {
        return Math.min(Math.max(value, lowLimit), highLimit);
    }   //clipRange

    /**
     * This method clips the given value to the range between -1.0 and 1.0.
     *
     * @param value specifies the value to be clipped
     * @return the result of the clipped value.
     */
    public static double clipRange(double value)
    {
        return clipRange(value, -1.0, 1.0);
    }   //clipRange
}
