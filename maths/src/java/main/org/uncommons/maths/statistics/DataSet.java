// ============================================================================
//   Copyright 2006, 2007 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package org.uncommons.maths.statistics;

/**
 * Utility class for calculating statistics for a finite data set.
 * @author Daniel Dyer
 */
public class DataSet
{
    private static final int DEFAULT_CAPACITY = 50;
    private static final double GROWTH_RATE = 1.5d;

    private double[] dataSet;
    private int dataSetSize = 0;

    private double total = 0;
    private double product = 1;


    /**
     * Creates an empty data set with a default initial capacity.
     */
    public DataSet()
    {
        this(DEFAULT_CAPACITY);
    }


    /**
     * Creates an empty data set with the specified initial capacity.
     * @param capacity The initial capacity for the data set (this number
     * of values will be able to be added without needing to resize the
     * internal data storage). 
     */
    public DataSet(int capacity)
    {
        this.dataSet = new double[capacity];
        this.dataSetSize = 0;
    }


    /**
     * Creates a data set and populates it with the specified values.
     * @param dataSet The values to add to this data set.
     */
    public DataSet(double[] dataSet)
    {
        this.dataSet = dataSet.clone();
        this.dataSetSize = dataSet.length;
        for (double value : this.dataSet)
        {
            updateStatsWithNewValue(value);
        }
    }


    /**
     * Adds a single value to the data set and updates any
     * statistics that are calculated cumulatively.
     * @param value The value to add.
     */
    public final void addValue(double value)
    {
        if (dataSetSize == dataSet.length)
        {
            // Increase the capacity of the array.
            int newLength = (int) (GROWTH_RATE * dataSetSize);
            double[] newDataSet = new double[newLength];
            System.arraycopy(dataSet, 0, newDataSet, 0, dataSetSize);
            dataSet = newDataSet;
        }
        dataSet[dataSetSize] = value;
        updateStatsWithNewValue(value);
        ++dataSetSize;
    }


    private void updateStatsWithNewValue(double value)
    {
        total += value;
        product *= value;
    }


    /**
     * Returns the number of values in this data set.
     * @return The size of the data set.
     */
    public final int getSize()
    {
        return dataSetSize;
    }


    /**
     * @return The sum of all values.
     */
    public final double getAggregate()
    {
        return total;
    }


    /**
     * @return The product of all values.
     */
    public final double getProduct()
    {
        return product;
    }


    /**
     * The arithemthic mean of an n-element set is the sum of
     * all the elements divided by n.
     * @see #getGeometricMean()
     * @return The arithmetic mean of all elements in the data set.
     */
    public final double getArithmeticMean()
    {
        return total / dataSetSize;
    }



    /**
     * The geometric mean of an n-element set is the nth-root of
     * the product of all the elements.
     * @see #getArithmeticMean()
     * @return The geometric mean of all elements in the data set.
     */
    public final double getGeometricMean()
    {
        return Math.pow(product, 1.0d / dataSetSize);
    }


    /**
     * Calculates the mean absolute deviation of the data set.  This
     * is the average (absolute) amount that a single value deviates from
     * the arithmetic mean.
     * @see #getArithmeticMean()
     * @see #getVariance()
     * @see #getStandardDeviation()
     * @return The mean absolute deviation of the data set.
     */
    public final double getMeanDeviation()
    {
        double mean = getArithmeticMean();
        double diffs = 0;
        for (int i = 0; i < dataSetSize; i++)
        {
            diffs += Math.abs(mean - dataSet[i]);
        }
        return diffs / dataSetSize;
    }



    /**
     * Calculates the variance (a measure of statistical dispersion)
     * of the data set.  There are different measures of variance
     * depending on whether the data set is itself a finite population
     * or is a sample from some larger population.  For large data sets
     * the difference is negligible. This method calculates the
     * population variance.
     * @see #getSampleVariance()
     * @see #getStandardDeviation()
     * @see #getMeanDeviation()
     * @return The population variance of the data set.
     */
    public double getVariance()
    {
        return sumSquaredDiffs() / getSize();
    }


    /**
     * Helper method for variance calculations.
     * @return The sum of the squares of the differences between
     * each value and the arithmetic mean.
     */
    private double sumSquaredDiffs()
    {
        double mean = getArithmeticMean();
        double squaredDiffs = 0;
        for (int i = 0; i < getSize(); i++)
        {
            double diff = mean - dataSet[i];
            squaredDiffs += (diff * diff);
        }
        return squaredDiffs;
    }


    /**
     * The standard deviation is the square root of the variance.
     * This method calculates the population standard deviation as
     * opposed to the sample standard deviation.  For large data
     * sets the difference is negligible.
     * @see #getSampleStandardDeviation()
     * @see #getVariance()
     * @see #getMeanDeviation()
     * @return The standard deviation of the population.
     */
    public final double getStandardDeviation()
    {
        return Math.sqrt(getVariance());
    }


    /**
     * Calculates the variance (a measure of statistical dispersion)
     * of the data set.  There are different measures of variance
     * depending on whether the data set is itself a finite population
     * or is a sample from some larger population.  For large data sets
     * the difference is negligible.  This method calculates the sample
     * variance.
     * @see #getVariance()
     * @see #getSampleStandardDeviation()
     * @see #getMeanDeviation()
     * @return The sample variance of the data set.
     */
    public double getSampleVariance()
    {
        return sumSquaredDiffs() / (getSize() - 1);
    }


    /**
     * The sample standard deviation is the square root of the
     * sample variance.  For large data sets the difference
     * between sample standard deviation and population standard
     * deviation is negligible.
     * @see #getStandardDeviation()
     * @see #getSampleVariance()
     * @see #getMeanDeviation()
     * @return The sample standard deviation of the data set.
     */
    public final double getSampleStandardDeviation()
    {
        return Math.sqrt(getSampleVariance());
    }
}
