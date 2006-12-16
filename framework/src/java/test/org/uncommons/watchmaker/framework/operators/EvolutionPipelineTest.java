// ============================================================================
//   Copyright 2006 Daniel W. Dyer
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
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Unit test for compound, sequential evolutionary schemes.
 * @author Daniel Dyer
 */
public class EvolutionPipelineTest
{
    /**
     * Make sure that multiple operators in a pipeline are applied correctly
     * to the population and validate the cumulative effects.
     */
    @Test
    public void testCompoundEvolution()
    {
        List<Integer> population = new ArrayList<Integer>(10);
        for (int i = 10; i <= 100; i += 10)
        {
            population.add(i);
        }
        // Increment 30% of the numbers and decrement the other 70%.
        List<EvolutionaryOperator<? super Integer>> operators = new ArrayList<EvolutionaryOperator<? super Integer>>(2);
        operators.add(new IntegerAdjuster(1));
        operators.add(new IntegerAdjuster(3));
        EvolutionPipeline<Integer> evolutionScheme = new EvolutionPipeline<Integer>(operators);
        population = evolutionScheme.apply(population, new MersenneTwisterRNG());
        // Net result should be each candidate increased by 4.
        int aggregate = 0;
        for (Integer i : population)
        {
            aggregate += i;
            assert (i % 10 == 4) : "Candidate should have increased by 4, is " + i;
        }
        assert aggregate == 590 : "Aggregate should be 590 after mutations, is " + aggregate;

    }
}
