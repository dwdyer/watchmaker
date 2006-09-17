// ============================================================================
//   Copyright 2006 Daniel W. Dyer
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
package uk.co.dandyer.maths.stats;

/**
 * Operations for calculating statistical measures for a set of values.
 * @author Daniel Dyer
 */
public interface DataSet
{
    /**
     * Adds a single value to the data set and updates any
     * statistics that are calculated cumulatively.
     */
    void addValue(double value);

    /**
     * Returns the number of values in this data set.
     * @return The size of the data set.
     */
    int getSize();

    /**
     * The arithemthic mean of an n-element set is the sum of
     * all the elements divided by n.
     * @see #getGeometricMean()
     */
    double getArithmeticMean();

    /**
     * The geometric mean of an n-element set is the nth-root of
     * the sum of all the elements.
     * @see #getArithmeticMean()
     */
    double getGeometricMean();

    /**
     * Calculates the variance (a measure of statistical dispersion)
     * of the data set.  There are different measures of variance
     * depending on whether the data set is itself a finite population
     * or is a sample from some larger population.
     * @see PopulationDataSet
     * @see SampleDataSet
     * @see #getStandardDeviation()
     * @see #getMeanDeviation()
     */
    double getVariance();

    /**
     * Calculates the mean absolute deviation of the data set.  This
     * is the average (absolute) amount that a single value deviates from
     * the arithmetic mean.
     * @see #getArithmeticMean()
     * @see #getVariance()
     * @see #getStandardDeviation()
     */
    double getMeanDeviation();

    /**
     * The standard deviation is the square root of the variance
     * (however the variance is calculated).
     * @see #getVariance()
     * @see #getMeanDeviation()
     */
    double getStandardDeviation();
}
