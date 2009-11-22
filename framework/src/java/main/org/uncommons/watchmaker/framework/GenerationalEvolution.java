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
package org.uncommons.watchmaker.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * <p>This class implements the evolution technique used by a typical generational
 * evolutionary algorithm.  It supports optional concurrent fitness evaluations
 * to take full advantage of multi-processor, multi-core and hyper-threaded machines.</p>
 *
 * <p>If multi-threading is enabled, evolution (mutation, cross-over, etc.) occurs
 * on the request thread but fitness evaluations are delegated to a pool of worker
 * threads. All of the host's available processing units are used (i.e. on a quad-core
 * machine, there will be four fitness evaluation worker threads).</p>
 *
 * <p>If multi-threading is disabled, all work is performed synchronously on the
 * request thread.  This strategy is suitable for restricted/managed environments where
 * it is not permitted for applications to manage their own threads.  If there are no
 * restrictions on concurrency, applications should enable it for improved performance.</p> 
 *
 * @author Daniel Dyer
 */
public class GenerationalEvolution<T> implements EvolutionAlgorithm<T>
{
    // A single multi-threaded worker is shared among multiple evolution engine instances.
    private static final FitnessEvaluationWorker WORKER = new FitnessEvaluationWorker();

    private final FitnessEvaluator<? super T> fitnessEvaluator;
    private final SelectionStrategy<? super T> selectionStrategy;
    private final EvolutionaryOperator<T> evolutionScheme;
    private final boolean multiThreaded;


    GenerationalEvolution(EvolutionaryOperator<T> evolutionScheme,
                          FitnessEvaluator<? super T> fitnessEvaluator,
                          SelectionStrategy<? super T> selectionStrategy,
                          boolean multiThreaded)
    {
        this.fitnessEvaluator = fitnessEvaluator;
        this.selectionStrategy = selectionStrategy;
        this.evolutionScheme = evolutionScheme;
        this.multiThreaded = multiThreaded;
    }


    /**
     * {@inheritDoc}
     */
    public List<EvaluatedCandidate<T>> evolvePopulation(List<EvaluatedCandidate<T>> evaluatedPopulation,
                                                        int eliteCount,
                                                        Random rng)
    {
        List<T> population = new ArrayList<T>(evaluatedPopulation.size());

        // First perform any elitist selection.
        List<T> elite = new ArrayList<T>(eliteCount);
        Iterator<EvaluatedCandidate<T>> iterator = evaluatedPopulation.iterator();
        while (elite.size() < eliteCount)
        {
            elite.add(iterator.next().getCandidate());
        }
        // Then select candidates that will be operated on to create the evolved
        // portion of the next generation.
        population.addAll(selectionStrategy.select(evaluatedPopulation,
                                                   fitnessEvaluator.isNatural(),
                                                   evaluatedPopulation.size() - eliteCount,
                                                   rng));
        // Then evolve the population.
        population = evolutionScheme.apply(population, rng);
        // When the evolution is finished, add the elite to the population.
        population.addAll(elite);
        return evaluatePopulation(population);
    }


    /**
     * {@inheritDoc}
     */
    public List<EvaluatedCandidate<T>> evaluatePopulation(List<T> population)
    {
        List<EvaluatedCandidate<T>> evaluatedPopulation = new ArrayList<EvaluatedCandidate<T>>(population.size());

        if (multiThreaded)
        {
            // Divide the required number of fitness evaluations equally among the
            // available processors and coordinate the threads so that we do not
            // proceed until all threads have finished processing.
            try
            {
                List<T> unmodifiablePopulation = Collections.unmodifiableList(population);
                List<Future<EvaluatedCandidate<T>>> results = new ArrayList<Future<EvaluatedCandidate<T>>>(population.size());
                // Submit tasks for execution and wait until all threads have finished fitness evaluations.
                for (T candidate : population)
                {
                    results.add(WORKER.submit(new FitnessEvalutationTask<T>(fitnessEvaluator,
                                                                            candidate,
                                                                            unmodifiablePopulation)));
                }
                for (Future<EvaluatedCandidate<T>> result : results)
                {
                    evaluatedPopulation.add(result.get());
                }
                assert evaluatedPopulation.size() == population.size() : "Wrong number of evaluated candidates.";
            }
            catch (ExecutionException ex)
            {
                throw new IllegalStateException("Fitness evaluation task execution failed.", ex);
            }
            catch (InterruptedException ex)
            {
                // Restore the interrupted status, allows methods further up the call-stack
                // to abort processing if appropriate.
                Thread.currentThread().interrupt();
            }
        }
        else // Do fitness evaluations on the request thread.
        {
            for (T candidate : population)
            {
                evaluatedPopulation.add(new EvaluatedCandidate<T>(candidate,
                                                                  fitnessEvaluator.getFitness(candidate, population)));
            }
        }

        return evaluatedPopulation;
    }
}
