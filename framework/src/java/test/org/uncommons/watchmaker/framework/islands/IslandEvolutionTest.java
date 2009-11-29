// ============================================================================
//   Copyright 2006-2009 Daniel W. Dyer
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
package org.uncommons.watchmaker.framework.islands;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.FrameworkTestUtils;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.factories.StubIntegerFactory;
import org.uncommons.watchmaker.framework.operators.IntegerAdjuster;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

/**
 * Unit test for the {@link IslandEvolution} class.
 * @author Daniel Dyer
 */
public class IslandEvolutionTest
{
    /**
     * This test makes sure that the evolution observer only gets invoked at the
     * end of each epoch, rather than each generation, and only once for the whole
     * island system rather than once for each island.
     */
    @Test
    public void testListeners()
    {
        FitnessEvaluator<Integer> fitnessEvaluator = new FitnessEvaluator<Integer>()
        {
            public double getFitness(Integer candidate, List<? extends Integer> population)
            {
                return 0;
            }

            public boolean isNatural()
            {
                return true;
            }
        };

        IslandEvolution<Integer> islandEvolution = new IslandEvolution<Integer>(3,
                                                                                new RingMigration(),
                                                                                new StubIntegerFactory(),
                                                                                new IntegerAdjuster(2),
                                                                                fitnessEvaluator,
                                                                                new RouletteWheelSelection(),
                                                                                FrameworkTestUtils.getRNG());
        final AtomicInteger invocationCount = new AtomicInteger(0);
        islandEvolution.addEvolutionObserver(new EvolutionObserver<Integer>()
        {
            public void populationUpdate(PopulationData<? extends Integer> populationData)
            {
                invocationCount.incrementAndGet();
            }
        });
        islandEvolution.evolve(5, 0, 5, 0, new GenerationCount(2));
        assert invocationCount.get() == 2 : "Listener should have been notified twice, was " + invocationCount.get();
    }
}
