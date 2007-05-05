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
package org.uncommons.maths.stats;

/**
 * Base class that implements most of the operations of the {@link DataSet}
 * interface.  The {@link #getVariance()} method is left to sub-classes to
 * implement and will affect the values returned by the {@link #getStandardDeviation()}
 * method.
 * @author Daniel Dyer
 */
public abstract class AbstractDataSet implements DataSet
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
    protected AbstractDataSet()
    {
        this.dataSet = new double[DEFAULT_CAPACITY];
        this.dataSetSize = 0;
    }


    /**
     * Creates a data set and populates it with the specified values.
     * @param dataSet The values to add to this data set.
     */
    protected AbstractDataSet(double[] dataSet)
    {
        this.dataSet = dataSet.clone();
        this.dataSetSize = dataSet.length;
        for (double value : this.dataSet)
        {
            updateStatsWithNewValue(value);
        }
    }


    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public final int getSize()
    {
        return dataSetSize;
    }


    /**
     * Retrieves a single value from the data set.
     * @param index The index of the value to retrieve.
     * @return The value at {@code index}.
     */
    protected final double getValue(int index)
    {
        if (index < 0 || index >= dataSetSize)
        {
            throw new IndexOutOfBoundsException("Invalid data set index: " + index);
        }
        return dataSet[index];
    }


    /**
     * {@inheritDoc}
     */
    public final double getAggregate()
    {
        return total;
    }


    /**
     * {@inheritDoc}
     */
    public final double getProduct()
    {
        return product;
    }


    /**
     * {@inheritDoc}
     */
    public final double getArithmeticMean()
    {
        return total / dataSetSize;
    }



    /**
     * {@inheritDoc}
     */
    public final double getGeometricMean()
    {
        return Math.pow(product, 1.0d / dataSetSize);
    }


    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public final double getStandardDeviation()
    {
        return Math.sqrt(getVariance());
    }
}
