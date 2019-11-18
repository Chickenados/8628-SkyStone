package chickenlib.util;

import android.icu.text.SimpleDateFormat;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

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

    /**
     * This method sums an array of numbers.
     *
     * @param nums specifies the array of numbers to be summed.
     * @return sum of the numbers.
     */
    public static double sum(double... nums)
    {
        double total = 0.0;

        for (double num : nums)
        {
            total += num;
        }

        return total;
    }   //sum

    /**
     * This method calculates and returns the average of the numbers in the given array.
     *
     * @param nums specifies the number array.
     * @return average of all numbers in the array. If the array is empty, return 0.
     */
    public static double average(double... nums)
    {
        return nums.length == 0 ? 0.0 : sum(nums) / nums.length;
    }   //average

    /**
     * This method returns the maximum magnitude of numbers in the specified array.
     *
     * @param nums specifies the number array.
     * @return maximum magnitude of the numbers in the array.
     */
    public static double maxMagnitude(double... nums)
    {
        double maxMag = Math.abs(nums[0]);

        for (double num : nums)
        {
            double magnitude = Math.abs(num);
            if (magnitude > maxMag)
            {
                maxMag = magnitude;
            }
        }

        return maxMag;
    }   //maxMagnitude

    /**
     * This method normalizes the given array of numbers such that no number exceeds +/- 1.0. If no number exceeds
     * the magnitude of 1.0, nothing will change, otherwise the original nums array will be modified in place.
     *
     * @param nums specifies the number array.
     */
    public static void normalizeInPlace(double[] nums)
    {
        double maxMag = maxMagnitude(nums);

        if (maxMag > 1.0)
        {
            for (int i = 0; i < nums.length; i++)
            {
                nums[i] /= maxMag;
            }
        }
    }   //normalizeInPlace

    /**
     * Rotate a point counter-clockwise about the origin.
     *
     * @param vector The vector to rotate.
     * @param angle  The angle in degrees to rotate by.
     * @return The vector after the rotation transformation.
     */
    public static RealVector rotateCCW(RealVector vector, double angle)
    {
        return createCCWRotationMatrix(angle).operate(vector);
    }   //rotateCCW

    /**
     * Rotate a point clockwise about the origin.
     *
     * @param vector The vector to rotate.
     * @param angle  The angle in degrees to rotate by.
     * @return The vector after the rotation transformation.
     */
    public static RealVector rotateCW(RealVector vector, double angle)
    {
        return createCWRotationMatrix(angle).operate(vector);
    }   //rotateCW

    /**
     * Create a rotation matrix that will rotate a point counter-clockwise
     * about the origin by a specific number of degrees.
     *
     * @param angle The angle in degrees to rotate by.
     * @return A rotation matrix describing a counter-clockwise rotation by <code>angle</code> degrees.
     */
    public static RealMatrix createCCWRotationMatrix(double angle)
    {
        double angleRad = Math.toRadians(angle);
        return MatrixUtils.createRealMatrix(
                new double[][] { { Math.cos(angleRad), -Math.sin(angleRad) }, { Math.sin(angleRad), Math.cos(angleRad) } });
    }   //createCCWRotationMatrix

    /**
     * Create a rotation matrix that will rotate a point clockwise
     * about the origin by a specific number of degrees.
     *
     * @param angle The angle in degrees to rotate by.
     * @return A rotation matrix describing a clockwise rotation by <code>angle</code> degrees.
     */
    public static RealMatrix createCWRotationMatrix(double angle)
    {
        return createCCWRotationMatrix(angle).transpose();
    }   //createCWRotationMatrix
}
