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

import org.testng.annotations.Test;
import org.uncommons.maths.Maths;

/**
 * Unit test for statistics class.
 * @author Daniel Dyer
 */
public class PopulationDataSetTest
{
    /**
     * Make sure that the data set's capacity grows correctly as
     * more values are added.
     */
    @Test
    public void testCapacityIncrease()
    {
        DataSet data = new PopulationDataSet(3);
        assert data.getSize() == 0 : "Initial size should be 0.";
        data.addValue(1);
        data.addValue(2);
        data.addValue(3);
        assert data.getSize() == 3 : "Size should be 3.";
        // Add a value to take the size beyond the initial capacity.
        data.addValue(4);
        assert data.getSize() == 4 : "Size should be 4.";
    }


    /**
     * Make sure that statistics generated from all of the values in the
     * data set are accurate.
     */
    @Test
    public void testAggregateStats()
    {
        DataSet data = new PopulationDataSet();
        data.addValue(1);
        data.addValue(2);
        data.addValue(3);
        data.addValue(4);
        data.addValue(5);
        long product = Maths.factorial(5);
        assert data.getAggregate() == 15d : "Incorrect aggregate: " + data.getAggregate();
        assert data.getProduct() == product : "Incorrect product: " + data.getProduct();
        assert data.getArithmeticMean() == 3d : "Incorrect average: " + data.getArithmeticMean();
        assert data.getMeanDeviation() == 1.2d : "Incorrect mean deviation: " + data.getMeanDeviation();
        assert data.getGeometricMean() == Math.pow(product, 0.2d)
                : "Incorrect geometric mean: " + data.getGeometricMean();
        assert data.getVariance() == 2d : "Incorrect variance: " + data.getVariance();
    }
}
