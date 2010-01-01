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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.testng.annotations.Test;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FrameworkTestUtils;

/**
 * Unit test for cross-over with arbitrary lists.
 * @author Daniel Dyer
 */
public class ListCrossoverTest
{
    @Test
    public void testCrossover()
    {
        EvolutionaryOperator<List<Integer>> crossover = new ListCrossover<Integer>();
        List<List<Integer>> population = new ArrayList<List<Integer>>(4);
        population.add(Arrays.asList(1, 2, 3, 4, 5));
        population.add(Arrays.asList(6, 7, 8, 9, 10));
        population.add(Arrays.asList(11, 12, 13, 14, 15));
        population.add(Arrays.asList(16, 17, 18, 19, 20));
        Set<Integer> values = new HashSet<Integer>(20);
        for (int i = 0; i < 20; i++)
        {
            population = crossover.apply(population, FrameworkTestUtils.getRNG());
            assert population.size() == 4 : "Population size changed after cross-over.";
            for (List<Integer> individual : population)
            {
                assert individual.size() == 5 : "Invalid candidate length: " + individual.size();
                values.addAll(individual);
            }
            // All of the individual elements should still be present, just jumbled up
            // between individuals.
            assert values.size() == 20 : "Information lost during cross-over.";
            values.clear();
        }
    }


    /**
     * When applied to lists of different lenghts, the {@link ListCrossover} operator
     * should pick a cross-over point that exists in both lists.  Therefore, the two
     * offspring will be the lengths of the two parents.
     */
    @Test
    public void testDifferentLengthParents()
    {
        EvolutionaryOperator<List<Integer>> crossover = new ListCrossover<Integer>(1, Probability.ONE);
        List<List<Integer>> population = new ArrayList<List<Integer>>(2);
        List<Integer> parent1 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        population.add(parent1);
        List<Integer> parent2 = Arrays.asList(9, 10, 11);
        population.add(parent2);

        List<List<Integer>> offspring = crossover.apply(population, FrameworkTestUtils.getRNG());
        assert offspring.size() == 2 : "Should be 2 offspring, is " + offspring.size();
        // Should be 1 child of length 8 and one of length 3.  Don't know which order though
        // as parents are shuffled before cross-over is applied.
        assert (offspring.get(0).size() == parent1.size() && offspring.get(1).size() == parent2.size())
               || (offspring.get(0).size() == parent2.size() && offspring.get(1).size() == parent1.size())
               : "Offspring are wrong lengths after cross-over.";
    }


    /**
     * When the probability determines that no cross-over should be performed, the two parents
     * should just be copied into the next generation unchanged.
     */
    @Test
    public void testZeroProbability()
    {
        EvolutionaryOperator<List<Integer>> crossover = new ListCrossover<Integer>(new ConstantGenerator<Integer>(1),
                                                                                   new ConstantGenerator<Probability>(Probability.ZERO));
        List<List<Integer>> population = new ArrayList<List<Integer>>(4);
        List<Integer> parent1 = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> parent2 = Arrays.asList(6, 7, 8, 9, 10);
        population.add(parent1);
        population.add(parent2);
        population = crossover.apply(population, FrameworkTestUtils.getRNG());
        assert population.contains(parent1) : "Parent should survive unaltered.";
        assert population.contains(parent2) : "Parent should survive unaltered.";
    }


    /**
     * If one or both of the parent lists has only one element, it can't participate in
     * meaningful cross-over.  In practice this situation is unlikely to occur (most
     * programs won't be evolving single-element lists), but the operator should handle
     * it gracefully.
     */
    @Test
    public void testParentTooShort()
    {
        EvolutionaryOperator<List<Integer>> crossover = new ListCrossover<Integer>(new ConstantGenerator<Integer>(1));
        List<List<Integer>> population = new ArrayList<List<Integer>>(2);
        List<Integer> parent1 = Arrays.asList(1, 2, 3);
        List<Integer> parent2 = Arrays.asList(4); // Too short for cross-over.
        population.add(parent1);
        population.add(parent2);
        population = crossover.apply(population, FrameworkTestUtils.getRNG());
        assert population.contains(parent1) : "Parent should survive unaltered.";
        assert population.contains(parent2) : "Parent should survive unaltered.";
    }
}
