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
package org.uncommons.watchmaker.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Generic evolutionary algorithm engine for evolution that runs
 * on a single host.  Includes support for parallel fitness evaluations
 * on multi-processor and multi-core machines.
 * @author Daniel Dyer
 * @param <T> The type of entity that is to be evolved.
 */
public class StandaloneEvolutionEngine<T> extends AbstractEvolutionEngine<T>
{
    private final int threadCount = Runtime.getRuntime().availableProcessors();

    /**
     * This thread pool performs concurrent fitness evaluations (on hosts that
     * have more than one processor).
     */
    private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(threadCount,
                                                                         threadCount,
                                                                         60,
                                                                         TimeUnit.SECONDS,
                                                                         new LinkedBlockingQueue<Runnable>(),
                                                                         new DaemonThreadFactory());


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
    public StandaloneEvolutionEngine(CandidateFactory<T> candidateFactory,
                                     EvolutionaryOperator<? super T> evolutionScheme,
                                     FitnessEvaluator<? super T> fitnessEvaluator,
                                     SelectionStrategy<? super T> selectionStrategy,
                                     Random rng)
    {
        super(candidateFactory, evolutionScheme, fitnessEvaluator, selectionStrategy, rng);
        int noOfThreads = threadPool.prestartAllCoreThreads();
        System.out.println("Standalone evolution engine initialised with " + noOfThreads + " threads.");
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
    protected List<EvaluatedCandidate<T>> evaluatePopulation(List<T> population)
    {
        List<EvaluatedCandidate<T>> evaluatedPopulation
            = Collections.synchronizedList(new ArrayList<EvaluatedCandidate<T>>(population.size()));

        // Divide the required number of fitness evaluations equally among the
        // available processors and coordinate the threads so that we do not
        // proceed until all threads have finished processing.
        try
        {
            // Make sure that we don't try to use more threads than we have candidates.
            int threadUtilisation = Math.min(threadCount, population.size());

            CountDownLatch latch = new CountDownLatch(threadUtilisation);
            int subListSize = population.size() / threadUtilisation;
            for (int i = 0; i < threadUtilisation; i++)
            {
                int fromIndex = i * subListSize;
                int toIndex = i < threadUtilisation - 1 ? fromIndex + subListSize : population.size();
                List<T> subList = population.subList(fromIndex, toIndex);
                threadPool.execute(new FitnessEvalutationTask(subList, evaluatedPopulation, latch));
            }
            latch.await(); // Wait until all threads have finished fitness evaluations.
        }
        catch (InterruptedException ex)
        {
            throw new IllegalStateException("Evolution aborted - concurrency failure.", ex);
        }
        assert evaluatedPopulation.size() == population.size() : "Wrong number of evaluated candidates.";

        // Sort candidates in descending order according to fitness.
        if (getFitnessEvaluator().isNatural()) // Descending values for natural fitness.
        {
            Collections.sort(evaluatedPopulation, Collections.reverseOrder());
        }
        else // Ascending values for non-natural fitness.
        {
            Collections.sort(evaluatedPopulation);
        }
        return evaluatedPopulation;
    }


    /**
     * Thread factory that creates daemon threads for use by the thread pool.
     */
    private static final class DaemonThreadFactory implements ThreadFactory
    {
        private static int threadCount = 0;

        public Thread newThread(Runnable runnable)
        {
            Thread thread = new Thread(runnable, "EvolutionEngine-" + threadCount++);
            thread.setDaemon(true);
            return thread;
        }
    }


    /**
     * Runnable task for performing parallel fitness evaluations.
     */
    private final class FitnessEvalutationTask implements Runnable
    {
        private final List<T> candidates;
        private final List<EvaluatedCandidate<T>> evaluatedPopulation;
        private final CountDownLatch latch;

        public FitnessEvalutationTask(List<T> candidates,
                                      List<EvaluatedCandidate<T>> evaluatedPopulation,
                                      CountDownLatch latch)
        {
            this.candidates = candidates;
            this.evaluatedPopulation = evaluatedPopulation;
            this.latch = latch;
        }


        public void run()
        {
            // Use a temporary unsynchronised list to store evaluated candidates and then
            // add them to the synchronised result list in one go at the end.  This avoids
            // contention when there are several threads trying repeatedly to add items to
            // the result list.
            List<EvaluatedCandidate<T>> evaluatedCandidates = new ArrayList<EvaluatedCandidate<T>>(candidates.size());
            for (T candidate : candidates)
            {
                evaluatedCandidates.add(new EvaluatedCandidate<T>(candidate,
                                                                  getFitnessEvaluator().getFitness(candidate)));
            }
            evaluatedPopulation.addAll(evaluatedCandidates);
            latch.countDown();
        }
    }
}
