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
package uk.co.dandyer.watchmaker.framework.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import uk.co.dandyer.maths.NumberSequence;

/**
 * Cross-over with a configurable number of points (fixed or random) for
 * arrays of primitive doubles.
 * @author Daniel Dyer
 */
public class DoubleArrayCrossover extends AbstractCrossover<double[]>
{
    /**
     * Default is single-point cross-over.
     */
    public DoubleArrayCrossover()
    {
        this(1);
    }


    /**
     * Cross-over with a fixed number of cross-over points.
     */
    public DoubleArrayCrossover(int crossoverPoints)
    {
        super(crossoverPoints);
    }


    /**
     * Cross-over with a variable number of cross-over points.
     */
    public DoubleArrayCrossover(NumberSequence<Integer> crossoverPointsVariable)
    {
        super(crossoverPointsVariable);
    }


    protected List<double[]> reproduce(double[] parent1,
                                       double[] parent2,
                                       int numberOfCrossoverPoints,
                                       Random rng)
    {
        if (parent1.length != parent2.length)
        {
            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
        }
        double[] offspring1 = new double[parent1.length];
        System.arraycopy(parent1, 0, offspring1, 0, parent1.length);
        double[] offspring2 = new double[parent2.length];
        System.arraycopy(parent2, 0, offspring2, 0, parent2.length);
        // Apply as many cross-overs as required.
        double[] temp = new double[parent1.length];
        for (int i = 0; i < numberOfCrossoverPoints; i++)
        {
            int crossoverIndex = rng.nextInt(parent1.length);
            System.arraycopy(offspring1, 0, temp, 0, crossoverIndex);
            System.arraycopy(offspring2, 0, offspring1, 0, crossoverIndex);
            System.arraycopy(temp, 0, offspring2, 0, crossoverIndex);
        }
        List<double[]> result = new ArrayList<double[]>(2);
        result.add(offspring1);
        result.add(offspring2);
        return result;
    }
}
