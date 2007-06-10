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
package org.uncommons.watchmaker.framework.interactive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.SelectionStrategy;

/**
 * Unit test for user-guided selection strategy.
 * @author Daniel Dyer
 */
public class InteractiveSelectionTest
{
    private final MersenneTwisterRNG rng = new MersenneTwisterRNG();

    @Test
    public void testSingleSelectionPerGeneration()
    {
        final int groupSize = 2;
        RandomConsole<Integer> console = new RandomConsole<Integer>(groupSize);
        SelectionStrategy<Integer> strategy = new InteractiveSelection<Integer>(console,
                                                                                groupSize,
                                                                                1);
        List<EvaluatedCandidate<Integer>> population = new ArrayList<EvaluatedCandidate<Integer>>(5);
        population.add(new EvaluatedCandidate<Integer>(1, 0));
        population.add(new EvaluatedCandidate<Integer>(2, 0));
        population.add(new EvaluatedCandidate<Integer>(3, 0));
        population.add(new EvaluatedCandidate<Integer>(4, 0));
        population.add(new EvaluatedCandidate<Integer>(5, 0));
        
        List<Integer> selection = strategy.select(population, true, 3, rng);
        assert selection.size() == 3 : "Incorrect selection size: " + selection.size();
        assert console.getSelectionCount() == 1 : "Wrong number of user selections: " + console.getSelectionCount();
        // All 3 selected individuals should be the same since the strategy doubles up
        // selections when configured to restrict the number of user choices per generation.
        assert selection.get(0) == selection.get(1) : "Incorrect selection.";
        assert selection.get(1) == selection.get(2) : "Incorrect selection.";
        assert selection.get(0) == selection.get(2) : "Incorrect selection.";
    }


    @Test
    public void testMultipleSelectionsPerGeneration()
    {
        final int groupSize = 5;
        RandomConsole<Integer> console = new RandomConsole<Integer>(groupSize);
        SelectionStrategy<Integer> strategy = new InteractiveSelection<Integer>(console,
                                                                                groupSize,
                                                                                3);
        List<EvaluatedCandidate<Integer>> population = new ArrayList<EvaluatedCandidate<Integer>>(5);
        population.add(new EvaluatedCandidate<Integer>(1, 0));
        population.add(new EvaluatedCandidate<Integer>(2, 0));
        population.add(new EvaluatedCandidate<Integer>(3, 0));
        population.add(new EvaluatedCandidate<Integer>(4, 0));
        population.add(new EvaluatedCandidate<Integer>(5, 0));
        
        List<Integer> selection = strategy.select(population, true, 3, rng);
        assert selection.size() == 3 : "Incorrect selection size.";
        assert console.getSelectionCount() == 3 : "Wrong number of user selections: " + console.getSelectionCount();
    }

    
    /**
     * Automated test console implementation that simply selects an
     * individual at random.
     */
    private final class RandomConsole<T> implements Console<T>
    {
        private final int expectedGroupSize;

        /** Count how many times the select method is called. */
        private int selectionCount = 0;

        public RandomConsole(int expectedGroupSize)
        {
            this.expectedGroupSize = expectedGroupSize;
        }


        public int select(List<T> renderedEntities)
        {
            assert renderedEntities.size() == expectedGroupSize : "Wrong selection group size.";
            ++selectionCount;
            return rng.nextInt(renderedEntities.size());
        }


        public int getSelectionCount()
        {
            return selectionCount;
        }
    }
}
