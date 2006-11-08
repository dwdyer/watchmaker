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
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.NumberGenerator;

/**
 * Cross-over with a configurable number of points (fixed or random) for
 * bit sets.
 * @author Daniel Dyer
 */
public class BitSetCrossover extends AbstractCrossover<BitSet>
{
    /**
     * Default is single-point cross-over.
     */
    public BitSetCrossover()
    {
        this(1);
    }


    /**
     * Cross-over with a fixed number of cross-over points.
     */
    public BitSetCrossover(int crossoverPoints)
    {
        super(crossoverPoints);
    }


    /**
     * Cross-over with a variable number of cross-over points.
     */
    public BitSetCrossover(NumberGenerator<Integer> crossoverPointsVariable)
    {
        super(crossoverPointsVariable);
    }


    protected List<BitSet> mate(BitSet parent1,
                                BitSet parent2,
                                int numberOfCrossoverPoints,
                                Random rng)
    {
        if (parent1.length() != parent2.length())
        {
            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
        }
        BitSet offspring1 = (BitSet) parent1.clone();
        BitSet offspring2 = (BitSet) parent2.clone();
        offspring2.or(parent2);
        // Apply as many cross-overs as required.
        for (int i = 0; i < numberOfCrossoverPoints; i++)
        {
            int crossoverIndex = rng.nextInt(parent1.length());
            for (int j = 0; j < crossoverIndex; j++)
            {
                boolean temp = offspring1.get(j);
                offspring1.set(j, offspring2.get(j));
                offspring2.set(j, temp);
            }
        }
        List<BitSet> result = new ArrayList<BitSet>(2);
        result.add(offspring1);
        result.add(offspring2);
        return result;
    }
}
