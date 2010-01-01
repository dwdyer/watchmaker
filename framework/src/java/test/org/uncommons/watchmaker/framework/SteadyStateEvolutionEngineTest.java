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
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;

/**
 * Unit test for the {@link SteadyStateEvolutionEngine} class.
 * @author Daniel Dyer
 */
public class SteadyStateEvolutionEngineTest
{
    /**
     * A single iteration should update only a single candidate.
     */
    @Test
    public void testIncrementalEvolution()
    {
        SteadyStateEvolutionEngine<Integer> steadyState = new SteadyStateEvolutionEngine<Integer>(new StubIntegerFactory(),
                                                                                                  new IntegerAdjuster(5),
                                                                                                  new NullFitnessEvaluator(),
                                                                                                  new RouletteWheelSelection(),
                                                                                                  1,
                                                                                                  true,
                                                                                                  FrameworkTestUtils.getRNG());
        @SuppressWarnings("unchecked")
        List<EvaluatedCandidate<Integer>> population = Arrays.asList(new EvaluatedCandidate<Integer>(1, 0),
                                                                     new EvaluatedCandidate<Integer>(1, 0),
                                                                     new EvaluatedCandidate<Integer>(1, 0),
                                                                     new EvaluatedCandidate<Integer>(1, 0),
                                                                     new EvaluatedCandidate<Integer>(1, 0));
        List<EvaluatedCandidate<Integer>> evaluatedPopulation = steadyState.nextEvolutionStep(population,
                                                                                              0,
                                                                                              FrameworkTestUtils.getRNG());
        assert evaluatedPopulation.size() == 5 : "Population size should be unchanged.";
        int unchangedCount = 0;
        for (EvaluatedCandidate<Integer> candidate : evaluatedPopulation)
        {
            if (candidate.getCandidate() == 1)
            {
                ++unchangedCount;
            }
        }
        assert unchangedCount == 4 : "Should be 4 out of 5 candidates unchanged, is " + unchangedCount;
    }


    /**
     * Even if the evolutionary operator generates multiple offspring, only a single individual should be
     * replaced if the forceSingleUpdate flag is set.
     */
    @Test
    public void testForcedSingleCandidateUpdate()
    {
        SteadyStateEvolutionEngine<Integer> steadyState = new SteadyStateEvolutionEngine<Integer>(new StubIntegerFactory(),
                                                                                                  new IntegerAdjuster(5),
                                                                                                  new NullFitnessEvaluator(),
                                                                                                  new RouletteWheelSelection(),
                                                                                                  2,
                                                                                                  true, // Force single update.
                                                                                                  FrameworkTestUtils.getRNG());
        @SuppressWarnings("unchecked")
        List<EvaluatedCandidate<Integer>> population = Arrays.asList(new EvaluatedCandidate<Integer>(1, 0),
                                                                     new EvaluatedCandidate<Integer>(1, 0),
                                                                     new EvaluatedCandidate<Integer>(1, 0),
                                                                     new EvaluatedCandidate<Integer>(1, 0),
                                                                     new EvaluatedCandidate<Integer>(1, 0));
        List<EvaluatedCandidate<Integer>> evaluatedPopulation = steadyState.nextEvolutionStep(population,
                                                                                              0,
                                                                                              FrameworkTestUtils.getRNG());
        assert evaluatedPopulation.size() == 5 : "Population size should be unchanged.";
        int unchangedCount = 0;
        for (EvaluatedCandidate<Integer> candidate : evaluatedPopulation)
        {
            if (candidate.getCandidate() == 1)
            {
                ++unchangedCount;
            }
        }
        assert unchangedCount == 4 : "Should be 4 out of 5 candidates unchanged, is " + unchangedCount;
    }



    @Test
    public void testElitism()
    {
        SteadyStateEvolutionEngine<Integer> steadyState = new SteadyStateEvolutionEngine<Integer>(new StubIntegerFactory(),
                                                                                                  new IntegerAdjuster(10),
                                                                                                  new NullFitnessEvaluator(),
                                                                                                  new RouletteWheelSelection(),
                                                                                                  1,
                                                                                                  true,
                                                                                                  FrameworkTestUtils.getRNG());
        @SuppressWarnings("unchecked")
        List<EvaluatedCandidate<Integer>> population = Arrays.asList(new EvaluatedCandidate<Integer>(1, 1),
                                                                     new EvaluatedCandidate<Integer>(2, 2),
                                                                     new EvaluatedCandidate<Integer>(3, 3),
                                                                     new EvaluatedCandidate<Integer>(4, 4),
                                                                     new EvaluatedCandidate<Integer>(5, 5));
        // The fittest candidate should always be preserved.
        for (int i = 0; i < 20; i++) // Once is not enough to be confident.
        {
            List<EvaluatedCandidate<Integer>> evaluatedPopulation = steadyState.nextEvolutionStep(population,
                                                                                                  1,
                                                                                                  FrameworkTestUtils.getRNG());
            assert evaluatedPopulation.size() == 5 : "Population size should be unchanged.";
            boolean found = false;
            for (EvaluatedCandidate<Integer> candidate : evaluatedPopulation)
            {
                if (candidate.getCandidate() == 5)
                {
                    found = true;
                    break;
                }
            }
            assert found : "Elite candidate should be preserved.";
        }
    }
}
