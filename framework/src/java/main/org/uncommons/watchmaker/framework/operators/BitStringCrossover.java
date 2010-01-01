//=============================================================================
// Copyright 2006-2010 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//=============================================================================
package org.uncommons.watchmaker.framework.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.binary.BitString;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;

/**
 * Cross-over with a configurable number of points (fixed or random) for
 * bit strings.
 * @see BitString
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
     * being copied, unchanged, into the output population.
     */
    public BitStringCrossover(int crossoverPoints, Probability crossoverProbability)
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
     * Sets up a cross-over implementation that uses a variable number of cross-over
     * points.  Cross-over is applied to a proportion of selected parent pairs, with
     * the remainder copied unchanged into the output population.  The size of this
     * evolved proportion is controlled by the {@code crossoverProbabilityVariable}
     * parameter.
     * @param crossoverPointsVariable A variable that provides a (possibly constant,
     * possibly random) number of cross-over points for each cross-over operation.
     * @param crossoverProbabilityVariable A variable that controls the probability
     * that, once selected, a pair of parents will be subjected to cross-over rather
     * than being copied, unchanged, into the output population.
     */
    public BitStringCrossover(NumberGenerator<Integer> crossoverPointsVariable,
                              NumberGenerator<Probability> crossoverProbabilityVariable)
    {
        super(crossoverPointsVariable, crossoverProbabilityVariable);
    }


    /**
     * {@inheritDoc}
     */
    @Override
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
            offspring1.swapSubstring(offspring2, 0, crossoverIndex);
        }
        List<BitString> result = new ArrayList<BitString>(2);
        result.add(offspring1);
        result.add(offspring2);
        return result;
    }
}
