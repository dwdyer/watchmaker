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
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FrameworkTestUtils;

/**
 * Unit test to validate the operation of the {@link ListOrderCrossover} operator.
 * @author Daniel Dyer
 */
public class ListOrderCrossoverTest
{
    @Test
    public void testCrossover()
    {
        EvolutionaryOperator<List<Integer>> operator = new ListOrderCrossover<Integer>();
        List<Integer> parent1 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        List<Integer> parent2 = Arrays.asList(3, 7, 5, 1, 6, 8, 2, 4);
        List<List<Integer>> population = new ArrayList<List<Integer>>(2);
        population.add(parent1);
        population.add(parent2);

        for (int i = 0; i < 50; i++) // Do several cross-overs to check different cross-over points.
        {
            population = operator.apply(population, FrameworkTestUtils.getRNG());
            for (List<Integer> offspring : population)
            {
                for (int j = 1; j <= 8; j++)
                {
                    assert offspring.contains(j) : "Evolved candidate missing required element " + j;
                }
            }
        }
    }


    /**
     * The {@link ListOrderCrossover} operator is only defined to work on populations
     * containing lists of equal lengths.  Any attempt to apply the operation to
     * populations that contain different length lists should throw an exception.
     * Not throwing an exception should be considered a bug since it could lead to
     * hard to trace bugs elsewhere.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDifferentLengthParents()
    {
        EvolutionaryOperator<List<Integer>> crossover
            = new ListOrderCrossover<Integer>(new ConstantGenerator<Probability>(Probability.ONE));
        List<List<Integer>> population = new ArrayList<List<Integer>>(2);
        population.add(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        population.add(Arrays.asList(9, 10, 11));
        // This should cause an exception since the parents are different lengths.
        crossover.apply(population, FrameworkTestUtils.getRNG());
    }
}
