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
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.maths.random.MersenneTwisterRNG;

/**
 * Unit test to validate the operation of the {@link ListOrderCrossover} operator.
 * @author Daniel Dyer
 */
public class ListOrderCrossoverTest
{
    @Test
    public void testCrossover()
    {
        ListOrderCrossover operator = new ListOrderCrossover();
        List<Integer> parent1 = new ArrayList<Integer>(8);
        parent1.add(1);
        parent1.add(2);
        parent1.add(3);
        parent1.add(4);
        parent1.add(5);
        parent1.add(6);
        parent1.add(7);
        parent1.add(8);
        List<Integer> parent2 = new ArrayList<Integer>(8);
        parent2.add(3);
        parent2.add(7);
        parent2.add(5);
        parent2.add(1);
        parent2.add(6);
        parent2.add(8);
        parent2.add(2);
        parent2.add(4);
        List<List<Integer>> population = Arrays.asList(parent1, parent2);

        for (int i = 0; i < 20; i++) // Do more than one cross-over to check different cross-over points.
        {
            population = operator.apply(population, new MersenneTwisterRNG());
            for (List<Integer> offspring : population)
            {
                for (int j = 1; j <= 8; j++)
                {
                    assert offspring.contains(j) : "Evolved candidate missing required element " + j;
                }
            }
        }
    }
}
