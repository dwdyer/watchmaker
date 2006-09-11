package uk.co.dandyer.maths;

import java.math.BigInteger;

/**
 * Maths operations not provided by {@link java.lang.Math}.
 * @author Daniel Dyer
 */
public final class Maths
{
    private Maths()
    {
        // Prevent instantiation.
    }


    /**
     * Calculates the factorial of n where n is a number in the
     * range 0 - 20.  Zero factorial is equal to 1.  For values of
     * n greater than 20 you must use {@link #bigFactorial(int)}.
     * @param n The factorial to calculate.
     * @return The factorial of n.
     * @see #bigFactorial(int)
     */
    public static long factorial(int n)
    {
        if (n < 0 || n > 20)
        {
            throw new IllegalArgumentException("Argument must be in the range 0 - 20.");
        }
        long factorial = 1;
        for (int i = n; i > 1; i--)
        {
            factorial *= i;
        }
        return factorial;
    }


    /**
     * Calculates the factorial of n where n is a positive integer.
     * Zero factorial is equal to 1.  For values of n up to 20, consider
     * using {@link #factorial(int)} instead since it uses a faster
     * implementation.
     * @param n The factorial to calculate.
     * @return The factorial of n.
     * @see #factorial(int)
     */
    public static BigInteger bigFactorial(int n)
    {
        if (n < 0)
        {
            throw new IllegalArgumentException("Argument must greater than or equal to zero.");
        }
        BigInteger factorial = BigInteger.ONE;
        for (int i = n; i > 1; i--)
        {
            factorial = factorial.multiply(BigInteger.valueOf(i));
        }
        return factorial;
    }


    /**
     * Calculate the first argument raised to the power of the second.
     */
    public static int raiseToPower(int value, int power)
    {
        int result = 1;
        for (int i = 0; i < power; i++)
        {
            result *= value;
        }
        return result;
    }


    /**
     * Calculate logarithms for arbitrary bases.
     */
    public static double log(double base, double arg)
    {
        // Use natural logarithms and change the base.
        return Math.log(arg) / Math.log(base);
    }
}
