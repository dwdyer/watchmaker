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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.testng.annotations.Test;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Unit test for cross-over with arrays of objects.
 * @author Daniel Dyer
 */
public class ObjectArrayCrossoverTest
{
    @Test
    public void testCrossover()
    {
        EvolutionaryOperator<String[]> crossover = new ObjectArrayCrossover<String>();
        List<String[]> population = new ArrayList<String[]>(4);
        population.add(new String[]{"1", "2", "3", "4", "5"});
        population.add(new String[]{"6", "7", "8", "9", "10"});
        population.add(new String[]{"11", "12", "13", "14", "15"});
        population.add(new String[]{"16", "17", "18", "19", "20"});
        Random rng = new MersenneTwisterRNG();
        Set<String> values = new HashSet<String>(20);
        for (int i = 0; i < 20; i++)
        {
            population = crossover.apply(population, rng);
            assert population.size() == 4 : "Population size changed after cross-over.";
            for (String[] individual : population)
            {
                assert individual.length == 5 : "Invalid candidate length: " + individual.length;
                for (String value : individual)
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
}
