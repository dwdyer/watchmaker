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
import org.uncommons.maths.NumberGenerator;

/**
 * Variable-point (fixed or random) cross-over for arbitrary lists.
 * @author Daniel Dyer
 */
public class ListCrossover extends AbstractCrossover<List<?>>
{
    /**
     * Default is single-point cross-over.
     */
    public ListCrossover()
    {
        this(1);
    }


    /**
     * Cross-over with a fixed number of cross-over points.
     */
    public ListCrossover(int crossoverPoints)
    {
        super(crossoverPoints);
    }


    /**
     * Cross-over with a variable number of cross-over points.
     */
    public ListCrossover(NumberGenerator<Integer> crossoverPointsVariable)
    {
        super(crossoverPointsVariable);
    }


    protected List<List<?>> mate(List<?> parent1,
                                 List<?> parent2,
                                 int numberOfCrossoverPoints,
                                 Random rng)
    {
        if (parent1.size() != parent2.size())
        {
            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
        }
        List<Object> offspring1 = new ArrayList<Object>(parent1); // Use a random-access list for performance.
        List<Object> offspring2 = new ArrayList<Object>(parent2);
        // Apply as many cross-overs as required.
        for (int i = 0; i < numberOfCrossoverPoints; i++)
        {
            // Cross-over index is always greater than zero and less than
            // the length of the parent so that we always pick a point that
            // will result in a meaningful cross-over.
            int crossoverIndex = (1 + rng.nextInt(parent1.size() - 1));
            for (int j = 0; j < crossoverIndex; j++)
            {
                Object temp = offspring1.get(j);
                offspring1.set(j, offspring2.get(j));
                offspring2.set(j, temp);
            }
        }
        List<List<?>> result = new ArrayList<List<?>>(2);
        result.add(offspring1);
        result.add(offspring2);
        return result;
    }
}
