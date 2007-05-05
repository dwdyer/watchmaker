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
package org.uncommons.watchmaker.framework.operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Implements ordered cross-over between arbitrary lists.  The algorithm is
 * the Partially Mapped Cross-over (PMX) algorithm. 
 * @author Daniel Dyer
 */
public class ListOrderCrossover extends AbstractCrossover<List<?>>
{
    public ListOrderCrossover()
    {
        super(2); // Requires exactly two cross-over points.
    }


    public ListOrderCrossover(double crossoverProbability)
    {
        super(2, crossoverProbability);
    }

    
    protected List<? extends List<?>> mate(List<?> parent1,
                                           List<?> parent2,
                                           int numberOfCrossoverPoints,
                                           Random rng)
    {
        assert numberOfCrossoverPoints == 2 : "Expected number of cross-over points to be 2.";

        if (parent1.size() != parent2.size())
        {
            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
        }

        List<Object> offspring1 = new ArrayList<Object>(parent1); // Use a random-access list for performance.
        List<Object> offspring2 = new ArrayList<Object>(parent2);

        int point1 = rng.nextInt(parent1.size());
        int point2 = rng.nextInt(parent1.size());

        if (point1 > point2)
        {
            int temp = point1;
            point1 = point2;
            point2 = temp;
        }

        Map<Object, Object> mapping1 = new HashMap<Object, Object>(parent1.size());
        Map<Object, Object> mapping2 = new HashMap<Object, Object>(parent1.size());
        for (int i = point1; i <= point2; i++)
        {
            Object item1 = offspring1.get(i);
            Object item2 = offspring2.get(i);
            offspring1.set(i, item2);
            offspring2.set(i, item1);
            mapping1.put(item1, item2);
            mapping2.put(item2, item1);
        }

        checkUnmappedElements(offspring1, mapping2, point1, point2);
        checkUnmappedElements(offspring2, mapping1, point1, point2);

        List<List<?>> result = new ArrayList<List<?>>(2);
        result.add(offspring1);
        result.add(offspring2);
        return result;
    }


    /**
     * Checks elements that are outside of the parially mapped section to
     * see if there are any duplicate items in the list.  If there are, they
     * are mapped appropriately.
     */
    private void checkUnmappedElements(List<Object> offspring,
                                       Map<Object, Object> mapping,
                                       int mappingStart,
                                       int mappingEnd)
    {
        for (int i = 0; i < mappingStart; i++)
        {
            Object mapped = offspring.get(i);
            while (mapping.containsKey(mapped))
            {
                mapped = mapping.get(mapped);
            }
            offspring.set(i, mapped);
        }
        for (int i = mappingEnd + 1; i < offspring.size(); i++)
        {
            Object mapped = offspring.get(i);
            while (mapping.containsKey(mapped))
            {
                mapped = mapping.get(mapped);
            }
            offspring.set(i, mapped);
        }
    }
}
