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

import org.testng.annotations.Test;
import org.uncommons.maths.Maths;

/**
 * Unit test for statistics class.
 * @author Daniel Dyer
 */
public class DataSetTest
{
    private static final double[] DATA_SET = new double[]{1, 2, 3, 4, 5};

    /**
     * Make sure that the data set's capacity grows correctly as
     * more values are added.
     */
    @Test
    public void testCapacityIncrease()
    {
        DataSet data = new DataSet(3);
        assert data.getSize() == 0 : "Initial size should be 0.";
        data.addValue(1);
        data.addValue(2);
        data.addValue(3);
        assert data.getSize() == 3 : "Size should be 3.";
        // Add a value to take the size beyond the initial capacity.
        data.addValue(4);
        assert data.getSize() == 4 : "Size should be 4.";
    }


    @Test
    public void testAggregate()
    {
        DataSet data = new DataSet(DATA_SET);
        assert Math.round(data.getAggregate()) == 15
            : "Incorrect aggregate: " + data.getAggregate();
    }


    @Test
    public void testProduct()
    {
        DataSet data = new DataSet(DATA_SET);
        long product = Maths.factorial(5);
        assert Math.round(data.getProduct()) == product
            : "Incorrect product: " + data.getProduct();
    }


    @Test
    public void testArithmeticMean()
    {
        DataSet data = new DataSet(DATA_SET);
        assert Math.round(data.getArithmeticMean()) == 3
            : "Incorrect average: " + data.getArithmeticMean();
    }


    @Test
    public void testGeometricMean()
    {
        DataSet data = new DataSet(DATA_SET);
        long product = Maths.factorial(5);
        assert data.getGeometricMean() == Math.pow(product, 0.2d)
            : "Incorrect geometric mean: " + data.getGeometricMean();
    }


    @Test
    public void testMeanDeviation()
    {
        DataSet data = new DataSet(DATA_SET);
        assert data.getMeanDeviation() == 1.2d
            : "Incorrect mean deviation: " + data.getMeanDeviation();
    }


    @Test
    public void testPopulationVariance()
    {
        DataSet data = new DataSet(DATA_SET);
        assert Math.round(data.getVariance()) == 2
            : "Incorrect population variance: " + data.getVariance();
    }


    @Test
    public void testSampleVariance()
    {
        DataSet data = new DataSet(DATA_SET);
        assert data.getSampleVariance() == 2.5d
            : "Incorrect sample variance: " + data.getSampleVariance();
    }


    @Test
    public void testPopulationStandardDeviation()
    {
        DataSet data = new DataSet(DATA_SET);
        assert data.getStandardDeviation() == Math.sqrt(2d)
            : "Incorrect sample variance: " + data.getStandardDeviation();
    }


    @Test
    public void testSampleStandardDeviation()
    {
        DataSet data = new DataSet(DATA_SET);
        assert data.getSampleStandardDeviation() == Math.sqrt(2.5d)
            : "Incorrect sample variance: " + data.getSampleStandardDeviation();
    }
}
