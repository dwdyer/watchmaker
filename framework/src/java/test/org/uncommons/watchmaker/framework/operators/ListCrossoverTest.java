// ============================================================================
//   Copyright 2006, 2007, 2008 Daniel W. Dyer
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.testng.annotations.Test;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.Probability;

/**
 * Unit test for cross-over with arbitrary lists.
 * @author Daniel Dyer
 */
public class ListCrossoverTest
{
    private final Random rng = new MersenneTwisterRNG();

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
            population = crossover.apply(population, rng);
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
     * The {@link ListCrossover} operator is only defined to work on populations
     * containing lists of equal lengths.  Any attempt to apply the operation to
     * populations that contain different length lists should throw an exception.
     * Not throwing an exception should be considered a bug since it could lead to
     * hard to trace bugs elsewhere.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDifferentLengthParents()
    {
        EvolutionaryOperator<List<Integer>> crossover = new ListCrossover<Integer>(1, Probability.ONE);
        List<List<Integer>> population = new ArrayList<List<Integer>>(2);
        population.add(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        population.add(Arrays.asList(9, 10, 11));
        // This should cause an exception since the parents are different lengths.
        crossover.apply(population, rng);
    }
}
