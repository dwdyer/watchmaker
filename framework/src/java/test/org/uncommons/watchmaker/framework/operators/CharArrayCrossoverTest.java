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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.testng.annotations.Test;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FrameworkTestUtils;

/**
 * Unit test for cross-over with character arrays.
 * @author Daniel Dyer
 */
public class CharArrayCrossoverTest
{
    @Test
    public void testCrossover()
    {
        EvolutionaryOperator<char[]> crossover = new CharArrayCrossover();
        List<char[]> population = new ArrayList<char[]>(4);
        population.add(new char[]{'a', 'b', 'c', 'd', 'e'});
        population.add(new char[]{'f', 'g', 'h', 'i', 'j'});
        population.add(new char[]{'k', 'l', 'm', 'n', 'o'});
        population.add(new char[]{'p', 'q', 'r', 's', 't'});
        Set<Character> values = new HashSet<Character>(20);
        for (int i = 0; i < 20; i++)
        {
            population = crossover.apply(population, FrameworkTestUtils.getRNG());
            assert population.size() == 4 : "Population size changed after cross-over.";
            for (char[] individual : population)
            {
                assert individual.length == 5 : "Invalid candidate length: " + individual.length;
                for (char value : individual)
                {
                    values.add(value);
                }
            }
            // All of the individual elements should still be present, just jumbled up
            // between individuals.
            assert values.size() == 20 : "Information lost during cross-over.";
            values.clear();
        }
    }


    /**
     * The {@link CharArrayCrossover} operator is only defined to work on populations
     * containing arrays of equal lengths.  Any attempt to apply the operation to
     * populations that contain different length arrays should throw an exception.
     * Not throwing an exception should be considered a bug since it could lead to
     * hard to trace bugs elsewhere.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDifferentLengthParents()
    {
        EvolutionaryOperator<char[]> crossover = new CharArrayCrossover(1, Probability.ONE);
        List<char[]> population = new ArrayList<char[]>(2);
        population.add(new char[]{'a', 'b', 'c', 'd', 'e'});
        population.add(new char[]{'f', 'g', 'h', 'i', 'j', 'k'});
        // This should cause an exception since the parents are different lengths.
        crossover.apply(population, FrameworkTestUtils.getRNG());
    }
}
