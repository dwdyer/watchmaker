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
package org.uncommons.watchmaker.framework;

import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.factories.StubIntegerFactory;
import org.uncommons.watchmaker.framework.operators.IntegerAdjuster;

/**
 * Unit test for {@link EvolutionStrategyEngine} class.
 * @author Daniel Dyer
 */
public class EvolutionStrategyEngineTest
{
    @Test
    public void testOnePlusOneEvolutionStrategy()
    {
        EvolutionStrategyEngine<Integer> engine = new EvolutionStrategyEngine<Integer>(new StubIntegerFactory(),
                                                                                       new IntegerAdjuster(-1),
                                                                                       new IntegerEvaluator(),
                                                                                       true,
                                                                                       1,
                                                                                       FrameworkTestUtils.getRNG());
        @SuppressWarnings("unchecked")
        List<EvaluatedCandidate<Integer>> population = Arrays.asList(new EvaluatedCandidate<Integer>(1, 1));

        List<EvaluatedCandidate<Integer>> evolvedPopulation
            = engine.nextEvolutionStep(population, 0, FrameworkTestUtils.getRNG());
        assert evolvedPopulation.size() == 1 : "Population size should be 1, is " + evolvedPopulation.size();
        // The offspring is less fit than the parent (due to the -1 operator) so the parent should be retained.
        assert evolvedPopulation.get(0).getCandidate() == 1 : "Wrong individual after evolution.";
    }


    @Test
    public void testOneCommaOneEvolutionStrategy()
    {
        EvolutionStrategyEngine<Integer> engine = new EvolutionStrategyEngine<Integer>(new StubIntegerFactory(),
                                                                                       new IntegerAdjuster(-1),
                                                                                       new IntegerEvaluator(),
                                                                                       false,
                                                                                       1,
                                                                                       FrameworkTestUtils.getRNG());
        @SuppressWarnings("unchecked")
        List<EvaluatedCandidate<Integer>> population = Arrays.asList(new EvaluatedCandidate<Integer>(1, 1));
        
        List<EvaluatedCandidate<Integer>> evolvedPopulation
            = engine.nextEvolutionStep(population, 0, FrameworkTestUtils.getRNG());
        assert evolvedPopulation.size() == 1 : "Population size should be 1, is " + evolvedPopulation.size();
        // The offspring is less fit than the parent (due to the -1 operator) but the parent is not allowed to survive.
        assert evolvedPopulation.get(0).getCandidate() == 0 : "Wrong individual after evolution.";
    }
}
