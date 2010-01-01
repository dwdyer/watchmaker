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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;

/**
 * Implements ordered cross-over between arbitrary lists.  The algorithm is
 * the Partially Mapped Cross-over (PMX) algorithm.
 * @param <T> The component type of the lists that are combined. 
 * @author Daniel Dyer
 */
public class ListOrderCrossover<T> extends AbstractCrossover<List<T>>
{
    /**
     * Creates a cross-over operator with a cross-over probability of 1.
     */
    public ListOrderCrossover()
    {
        this(Probability.ONE);
    }


    /**
     * Creates a cross-over operator with the specified cross-over probability.
     * @param crossoverProbability The probability that cross-over will be performed
     * for any given pair.
     */
    public ListOrderCrossover(Probability crossoverProbability)
    {
        super(2, // Requires exactly two cross-over points.
              crossoverProbability);
    }


    /**
     * Creates a cross-over operator where cross-over may or may not be applied to a
     * given pair of parents depending on the {@code crossoverProbability}.
     * @param crossoverProbabilityVariable The probability that, once selected,
     * a pair of parents will be subjected to cross-over rather than
     * being copied, unchanged, into the output population.
     */
    public ListOrderCrossover(NumberGenerator<Probability> crossoverProbabilityVariable)
    {
        super(new ConstantGenerator<Integer>(2), // Requires exactly two cross-over points.
              crossoverProbabilityVariable);
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
        assert numberOfCrossoverPoints == 2 : "Expected number of cross-over points to be 2.";

        if (parent1.size() != parent2.size())
        {
            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
        }

        List<T> offspring1 = new ArrayList<T>(parent1); // Use a random-access list for performance.
        List<T> offspring2 = new ArrayList<T>(parent2);

        int point1 = rng.nextInt(parent1.size());
        int point2 = rng.nextInt(parent1.size());

        int length = point2 - point1;
        if (length < 0)
        {
            length += parent1.size();
        }

        Map<T, T> mapping1 = new HashMap<T, T>(length * 2); // Big enough map to avoid re-hashing.
        Map<T, T> mapping2 = new HashMap<T, T>(length * 2);
        for (int i = 0; i < length; i++)
        {
            int index = (i + point1) % parent1.size();
            T item1 = offspring1.get(index);
            T item2 = offspring2.get(index);
            offspring1.set(index, item2);
            offspring2.set(index, item1);
            mapping1.put(item1, item2);
            mapping2.put(item2, item1);
        }

        checkUnmappedElements(offspring1, mapping2, point1, point2);
        checkUnmappedElements(offspring2, mapping1, point1, point2);

        List<List<T>> result = new ArrayList<List<T>>(2);
        result.add(offspring1);
        result.add(offspring2);
        return result;
    }


    /**
     * Checks elements that are outside of the partially mapped section to
     * see if there are any duplicate items in the list.  If there are, they
     * are mapped appropriately.
     */
    private void checkUnmappedElements(List<T> offspring,
                                       Map<T, T> mapping,
                                       int mappingStart,
                                       int mappingEnd)
    {
        for (int i = 0; i < offspring.size(); i++)
        {
            if (!isInsideMappedRegion(i, mappingStart, mappingEnd))
            {
                T mapped = offspring.get(i);
                while (mapping.containsKey(mapped))
                {
                    mapped = mapping.get(mapped);
                }
                offspring.set(i, mapped);
            }
        }
    }


    /**
     * Checks whether a given list position is within the partially mapped
     * region used for cross-over.
     * @param position The list position to check.
     * @param startPoint The starting index (inclusive) of the mapped region.
     * @param endPoint The end index (exclusive) of the mapped region.
     * @return True if the specified position is in the mapped region, false
     * otherwise.
     */
    private boolean isInsideMappedRegion(int position,
                                         int startPoint,
                                         int endPoint)
    {
        boolean enclosed = (position < endPoint && position >= startPoint);
        boolean wrapAround = (startPoint > endPoint && (position >= startPoint || position < endPoint)); 
        return enclosed || wrapAround;
    }
}
