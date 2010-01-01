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
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;

/**
 * Variable-point (fixed or random) cross-over for arbitrary lists.
 * @param <T> The component type of the lists that are combined.
 * @author Daniel Dyer
 */
public class ListCrossover<T> extends AbstractCrossover<List<T>>
{
    /**
     * Default is single-point cross-over, applied to all parents.
     */
    public ListCrossover()
    {
        this(1);
    }


    /**
     * Cross-over with a fixed number of cross-over points.
     * @param crossoverPoints The constant number of cross-over points
     * to use for all cross-over operations.
     */
    public ListCrossover(int crossoverPoints)
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
    public ListCrossover(int crossoverPoints, Probability crossoverProbability)
    {
        super(crossoverPoints, crossoverProbability);
    }


    /**
     * Cross-over with a variable number of cross-over points.
     * @param crossoverPointsVariable A random variable that provides a number
     * of cross-over points for each cross-over operation.
     */
    public ListCrossover(NumberGenerator<Integer> crossoverPointsVariable)
    {
        super(crossoverPointsVariable);
    }


    /**
     * Cross-over with a variable number of cross-over points.  Cross-over
     * may or may not be applied to a given pair of parents depending on
     * the {@code crossoverProbability}.
     * @param crossoverPointsVariable A random variable that provides a number
     * of cross-over points for each cross-over operation.
     * @param crossoverProbabilityVariable The probability that, once selected,
     * a pair of parents will be subjected to cross-over rather than
     * being copied, unchanged, into the output population.
     */
    public ListCrossover(NumberGenerator<Integer> crossoverPointsVariable,
                         NumberGenerator<Probability> crossoverProbabilityVariable)
    {
        super(crossoverPointsVariable, crossoverProbabilityVariable);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected List<List<T>> mate(List<T> parent1,
                                 List<T> parent2,
                                 int numberOfCrossoverPoints,
                                 Random rng)
    {
        List<T> offspring1 = new ArrayList<T>(parent1); // Use a random-access list for performance.
        List<T> offspring2 = new ArrayList<T>(parent2);
        // Apply as many cross-overs as required.
        for (int i = 0; i < numberOfCrossoverPoints; i++)
        {
            // Cross-over index is always greater than zero and less than
            // the length of the parent so that we always pick a point that
            // will result in a meaningful cross-over.
            int max = Math.min(parent1.size(), parent2.size());
            if (max > 1) // Don't perform cross-over if there aren't at least 2 elements in each list.
            {
                int crossoverIndex = (1 + rng.nextInt(max - 1));
                for (int j = 0; j < crossoverIndex; j++)
                {
                    T temp = offspring1.get(j);
                    offspring1.set(j, offspring2.get(j));
                    offspring2.set(j, temp);
                }
            }
        }
        List<List<T>> result = new ArrayList<List<T>>(2);
        result.add(offspring1);
        result.add(offspring2);
        return result;
    }
}
