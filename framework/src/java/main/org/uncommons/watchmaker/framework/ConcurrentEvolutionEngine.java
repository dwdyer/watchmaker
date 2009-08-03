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
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.uncommons.watchmaker.framework.interactive.InteractiveSelection;

/**
 * <p>Multi-threaded generational {@link EvolutionEngine}.  Fitness evaluations
 * are performed in parallel on multi-processor, multi-core and hyper-threaded
 * machines.
 * Evolution (mutation, cross-over, etc.) occurs on the request thread but
 * fitness evaluations are delegated to a pool of worker threads.
 * All of the host's available processing units are used (i.e. on a quad-core
 * machine, there will be four fitness evaluation worker threads).</p>
 * 
 * <p>This evolution engine is the most suitable for typical evolutionary
 * algorithms.  Evolutionary programs that execute in a restricted/managed
 * environment that does not permit applications to manage their own
 * threads should use the {@link SequentialEvolutionEngine} instead.</p>
 * @param <T> The type of entity that is to be evolved.
 * @author Daniel Dyer
 * @see SequentialEvolutionEngine
 * @see CandidateFactory
 * @see FitnessEvaluator
 * @see SelectionStrategy
 * @see EvolutionaryOperator
 */
public class ConcurrentEvolutionEngine<T> extends AbstractEvolutionEngine<T>
{
    private final FitnessEvaluationWorker worker = new FitnessEvaluationWorker();

    
    /**
     * Creates a new evolution engine by specifying the various components required by
     * an evolutionary algorithm.
     * @param candidateFactory Factory used to create the initial population that is
     * iteratively evolved.
     * @param evolutionScheme The combination of evolutionary operators used to evolve
     * the population at each generation.
     * @param fitnessEvaluator A function for assigning fitness scores to candidate
     * solutions.
     * @param selectionStrategy A strategy for selecting which candidates survive to
     * be evolved.
     * @param rng The source of randomness used by all stochastic processes (including
     * evolutionary operators and selection strategies).
     */
    public ConcurrentEvolutionEngine(CandidateFactory<T> candidateFactory,
                                     EvolutionaryOperator<T> evolutionScheme,
                                     FitnessEvaluator<? super T> fitnessEvaluator,
                                     SelectionStrategy<? super T> selectionStrategy,
                                     Random rng)
    {
        super(candidateFactory, evolutionScheme, fitnessEvaluator, selectionStrategy, rng);
    }



    /**
     * Creates a new evolution engine for an interactive evolutionary algorithm.  It
     * is not necessary to specify a fitness evaluator for interactive evolution.
     * @param candidateFactory Factory used to create the initial population that is
     * iteratively evolved.
     * @param evolutionScheme The combination of evolutionary operators used to evolve
     * the population at each generation.
     * @param selectionStrategy Interactive selection strategy configured with appropriate
     * console.
     * @param rng The source of randomness used by all stochastic processes (including
     * evolutionary operators and selection strategies).
     */
    public ConcurrentEvolutionEngine(CandidateFactory<T> candidateFactory,
                                     EvolutionaryOperator<T> evolutionScheme,
                                     InteractiveSelection<T> selectionStrategy,
                                     Random rng)
    {
        this(candidateFactory,
             evolutionScheme,
             new NullFitnessEvaluator(), // No fitness evaluations to perform.
             selectionStrategy,
             rng);
    }


    /**
     * Takes a population, assigns a fitness score to each member and returns
     * the members with their scores attached, sorted in descending order of
     * fitness (descending order of fitness score for natural scores, ascending
     * order of scores for non-natural scores).
     * @param population The population to evaluate (each candidate is assigned
     * a fitness score).
     * @return The evaluated population (a list of candidates with attached fitness
     * scores).
     */
    @Override
    protected List<EvaluatedCandidate<T>> evaluatePopulation(List<T> population)
    {
        List<EvaluatedCandidate<T>> evaluatedPopulation = new ArrayList<EvaluatedCandidate<T>>(population.size());

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
                results.add(worker.submit(new FitnessEvalutationTask<T>(getFitnessEvaluator(),
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

        return evaluatedPopulation;
    }
}
