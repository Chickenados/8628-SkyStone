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

    public static double round(double value, int places){
        if(places < 0) throw new IllegalArgumentException("Attempted to round to illegal decimal places!");

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static class CknLoopCounter{

        private static CknLoopCounter instance;

        public static CknLoopCounter getInstance() {
            if(instance == null){
                instance = new CknLoopCounter();
            }
            return instance;
        }

        public long loop;

        public long getLoopCount(){ return loop; }
    }
}
