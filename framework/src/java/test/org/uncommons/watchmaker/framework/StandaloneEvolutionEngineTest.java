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
package org.uncommons.watchmaker.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.ElapsedTime;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

/**
 * Unit-test for the single-node evolution engine.
 * @author Daniel Dyer
 */
public class StandaloneEvolutionEngineTest
{
    private final Random rng = new MersenneTwisterRNG();
    private EvolutionEngine<Integer> engine;

    @BeforeMethod
    public void prepareEngine()
    {
        engine = new StandaloneEvolutionEngine<Integer>(new IntegerFactory(),
                                                        new IntegerZeroMaker(),
                                                        new IntegerEvaluator(),
                                                        new RouletteWheelSelection(),
                                                        rng);        
    }


    @Test
    public void testElitism()
    {
        class ElitismObserver implements EvolutionObserver<Integer>
        {
            private PopulationData<Integer> data;

            public void populationUpdate(PopulationData<Integer> data)
            {
                this.data = data;
            }

            public double getAverageFitness()
            {
                return data.getMeanFitness();
            }
        }
        ElitismObserver observer = new ElitismObserver();
        engine.addEvolutionObserver(observer);
        List<Integer> elite = new ArrayList<Integer>(3);
        // Add the following seed candidates, all better than any others that can possibly
        // get into the population (since every other candidate will always be zero).
        elite.add(7); // This candidate should be discarded by elitism.
        elite.add(11);
        elite.add(13);
        engine.evolve(10,
                      2,
                      elite,
                      new GenerationCount(2)); // Do at least 2 generations because the first is just the initial population.
        // Then when we have run the evolution, if the elite canidates were preserved they will
        // lift the average fitness above zero.  The exact value of the expected average fitness
        // is easy to calculate, it is the aggregate fitness divided by the population size.
        assert observer.getAverageFitness() == 24d / 10 : "Elite candidates not preserved correctly: " + observer.getAverageFitness();
        engine.removeEvolutionObserver(observer);
    }


    /**
     * The number of candidates preserved by elitism must be less than the total
     * population size.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testEliteCountTooHigh()
    {
        engine.evolve(10, 10, new GenerationCount(10)); // Should throw exception because elite is too big.
    }


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNoTerminationConditions()
    {
        engine.evolve(10, 0); // Should throw exception because there are no termination conditions.
    }


    @Test
    public void testInterrupt()
    {
        final long timeout = 1000L;
        final Thread requestThread = Thread.currentThread();
        engine.addEvolutionObserver(new EvolutionObserver<Integer>()
        {
            public void populationUpdate(PopulationData<Integer> populationData)
            {
                if (populationData.getElapsedTime() > timeout / 2)
                {
                    requestThread.interrupt();
                }
            }
        });
        long startTime = System.currentTimeMillis();
        engine.evolve(10, 0, new ElapsedTime(timeout));
        long elapsedTime = System.currentTimeMillis() - startTime;
        assert Thread.interrupted() : "Thread was not interrupted before timeout.";
        assert elapsedTime < timeout : "Engine did not respond to interrupt before timeout.";
    }


    /**
     * Stub candidate factory for tests.  Always returns zero-valued integers.
     */
    private static final class IntegerFactory extends AbstractCandidateFactory<Integer>
    {
        protected Integer generateRandomCandidate(Random rng)
        {
            return 0;
        }
    }


    /**
     * Trivial fitness evaluator for integers.  Used by tests above.
     */
    private static final class IntegerEvaluator implements FitnessEvaluator<Integer>
    {

        public double getFitness(Integer candidate,
                                 List<? extends Integer> population)
        {
            return candidate;
        }

        public boolean isNatural()
        {
            return true;
        }
    }


    /**
     * Trivial test operator that mutates all integers into zeroes.
     */
    @SuppressWarnings("unchecked")
    private static final class IntegerZeroMaker implements EvolutionaryOperator<Integer>
    {        
        public <S extends Integer> List<S> apply(List<S> selectedCandidates, Random rng)
        {
            List<S> result = new ArrayList<S>(selectedCandidates.size());
            for (int i = 0; i < selectedCandidates.size(); i++)
            {
                result.add((S) Integer.valueOf(0));
            }
            return result;
        }
    }
}
