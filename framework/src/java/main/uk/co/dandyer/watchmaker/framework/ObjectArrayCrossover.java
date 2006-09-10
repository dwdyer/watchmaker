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
package uk.co.dandyer.watchmaker.framework;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.lang.reflect.Array;
import uk.co.dandyer.maths.random.RandomSequence;

/**
 * Cross-over with a configurable number of points (fixed or random) for
 * arrays of reference types.
 * @author Daniel Dyer
 */
public class ObjectArrayCrossover extends AbstractCrossover<Object[]>
{
    /**
     * Default is single-point cross-over.
     */
    public ObjectArrayCrossover()
    {
        this(1);
    }


    /**
     * Cross-over with a fixed number of cross-over points.
     */
    public ObjectArrayCrossover(int crossoverPoints)
    {
        super(crossoverPoints);
    }


    /**
     * Cross-over with a variable number of cross-over points.
     */
    public ObjectArrayCrossover(RandomSequence<Integer> crossoverPointsVariable)
    {
        super(crossoverPointsVariable);
    }


    protected List<Object[]> reproduce(Object[] parent1,
                                       Object[] parent2,
                                       int numberOfCrossoverPoints,
                                       Random rng)
    {
        if (parent1.length != parent2.length)
        {
            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
        }
        // Create the most specific-type arrays possible.
        Object[] offspring1 = (Object[]) Array.newInstance(parent1.getClass().getComponentType(), parent1.length);
        System.arraycopy(parent1, 0, offspring1, 0, parent1.length);
        Object[] offspring2 = (Object[]) Array.newInstance(parent2.getClass().getComponentType(), parent2.length);
        System.arraycopy(parent2, 0, offspring2, 0, parent2.length);
        // Apply as many cross-overs as required.
        Object[] temp = new Object[parent1.length];
        for (int i = 0; i < numberOfCrossoverPoints; i++)
        {
            int crossoverIndex = rng.nextInt(parent1.length);
            System.arraycopy(offspring1, 0, temp, 0, crossoverIndex);
            System.arraycopy(offspring2, 0, offspring1, 0, crossoverIndex);
            System.arraycopy(temp, 0, offspring2, 0, crossoverIndex);
        }
        List<Object[]> result = new ArrayList<Object[]>(2);
        result.add(offspring1);
        result.add(offspring2);
        return result;
    }
}
