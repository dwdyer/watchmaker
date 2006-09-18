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
package org.uncommons.watchmaker.framework.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.NumberSequence;

/**
 * Cross-over with a configurable number of points (fixed or random) for
 * arrays of primitive booleans.
 * @author Daniel Dyer
 */
public class BooleanArrayCrossover extends AbstractCrossover<boolean[]>
{
    /**
     * Default is single-point cross-over.
     */
    public BooleanArrayCrossover()
    {
        this(1);
    }


    /**
     * Cross-over with a fixed number of cross-over points.
     */
    public BooleanArrayCrossover(int crossoverPoints)
    {
        super(crossoverPoints);
    }


    /**
     * Cross-over with a variable number of cross-over points.
     */
    public BooleanArrayCrossover(NumberSequence<Integer> crossoverPointsVariable)
    {
        super(crossoverPointsVariable);
    }


    protected List<boolean[]> reproduce(boolean[] parent1,
                                        boolean[] parent2,
                                        int numberOfCrossoverPoints,
                                        Random rng)
    {
        if (parent1.length != parent2.length)
        {
            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
        }
        boolean[] offspring1 = new boolean[parent1.length];
        System.arraycopy(parent1, 0, offspring1, 0, parent1.length);
        boolean[] offspring2 = new boolean[parent2.length];
        System.arraycopy(parent2, 0, offspring2, 0, parent2.length);
        // Apply as many cross-overs as required.
        boolean[] temp = new boolean[parent1.length];
        for (int i = 0; i < numberOfCrossoverPoints; i++)
        {
            int crossoverIndex = rng.nextInt(parent1.length);
            System.arraycopy(offspring1, 0, temp, 0, crossoverIndex);
            System.arraycopy(offspring2, 0, offspring1, 0, crossoverIndex);
            System.arraycopy(temp, 0, offspring2, 0, crossoverIndex);
        }
        List<boolean[]> result = new ArrayList<boolean[]>(2);
        result.add(offspring1);
        result.add(offspring2);
        return result;
    }
}
