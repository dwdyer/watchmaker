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
import org.uncommons.watchmaker.framework.types.BitString;

/**
 * Cross-over with a configurable number of points (fixed or random) for
 * bit sets.
 * @author Daniel Dyer
 */
public class BitStringCrossover extends AbstractCrossover<BitString>
{
    /**
     * Default is single-point cross-over, applied to all parents.
     */
    public BitStringCrossover()
    {
        this(1);
    }


    /**
     * Cross-over with a fixed number of cross-over points.
     * @param crossoverPoints The constant number of cross-over points
     * to use for all cross-over operations.
     */
    public BitStringCrossover(int crossoverPoints)
    {
        super(crossoverPoints);
    }


    /**
     * Cross-over with a fixed number of cross-over points.  Cross-over
     * may or may not be applied to a given pair of parents depending on
     * the {@code crossoverProbability}.
     * @param crossoverPoints The constant number of cross-over points
     * to use for all cross-over operations.
     * @param crossoverProbability The probability that, once selected,
     * a pair of parents will be subjected to cross-over rather than
     * being copied, unchanged, into the output population.  Must be in the range
     * {@literal 0 < crossoverProbability <= 1}
     */
    public BitStringCrossover(int crossoverPoints, double crossoverProbability)
    {
        super(crossoverPoints, crossoverProbability);
    }


    /**
     * Cross-over with a variable number of cross-over points.
     * @param crossoverPointsVariable A random variable that provides a number
     * of cross-over points for each cross-over operation.
     */
    public BitStringCrossover(NumberGenerator<Integer> crossoverPointsVariable)
    {
        super(crossoverPointsVariable);
    }


    /**
     * Cross-over with a variable number of cross-over points.  Cross-over
     * may or may not be applied to a given pair of parents depending on
     * the {@code crossoverProbability}.
     * @param crossoverPointsVariable A random variable that provides a number
     * of cross-over points for each cross-over operation.
     * @param crossoverProbability The probability that, once selected,
     * a pair of parents will be subjected to cross-over rather than
     * being copied, unchanged, into the output population.  Must be in the range
     * {@literal 0 < crossoverProbability <= 1}
     */
    public BitStringCrossover(NumberGenerator<Integer> crossoverPointsVariable,
                              double crossoverProbability)
    {
        super(crossoverPointsVariable, crossoverProbability);
    }


    protected List<BitString> mate(BitString parent1,
                                   BitString parent2,
                                   int numberOfCrossoverPoints,
                                   Random rng)
    {
        if (parent1.getLength() != parent2.getLength())
        {
            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
        }
        BitString offspring1 = parent1.clone();
        BitString offspring2 = parent2.clone();
        // Apply as many cross-overs as required.
        for (int i = 0; i < numberOfCrossoverPoints; i++)
        {
            // Cross-over index is always greater than zero and less than
            // the length of the parent so that we always pick a point that
            // will result in a meaningful cross-over.
            int crossoverIndex = (1 + rng.nextInt(parent1.getLength() - 1));
            for (int j = 0; j < crossoverIndex; j++)
            {
                boolean temp = offspring1.getBit(j);
                offspring1.setBit(j, offspring2.getBit(j));
                offspring2.setBit(j, temp);
            }
        }
        List<BitString> result = new ArrayList<BitString>(2);
        result.add(offspring1);
        result.add(offspring2);
        return result;
    }
}
