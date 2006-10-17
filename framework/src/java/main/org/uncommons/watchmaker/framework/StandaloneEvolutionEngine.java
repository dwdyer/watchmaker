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

    private final FitnessEvaluator<? super T> fitnessEvaluator;

    public StandaloneEvolutionEngine(CandidateFactory<T> candidateFactory,
                                     List<EvolutionaryOperator<T>> evolutionPipeline,
                                     FitnessEvaluator<? super T> fitnessEvaluator,
                                     SelectionStrategy selectionStrategy,
                                     Random rng)
    {
        super(candidateFactory, evolutionPipeline, selectionStrategy, rng);
        this.fitnessEvaluator = fitnessEvaluator;
        int threadCount = threadPool.prestartAllCoreThreads();
        System.out.println("Standalone evolution engine initialised with " + threadCount + " threads.");
    }


    /**
     * Takes a population, assigns a fitness score to each member and returns
     * the members with their scores attached, sorted in descending order of
     * fitness (descending order of fitness score for normalised scores, ascending
     * order of scores for de-normalised scores).
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
            int threadUtilisation = threadCount <= population.size() ? threadCount : population.size();

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
        if (fitnessEvaluator.isFitnessNormalised()) // Descending values for normalised fitness.
        {
            Collections.sort(evaluatedPopulation, Collections.reverseOrder());
        }
        else // Ascending values for de-normalised fitness.
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
                                                                  fitnessEvaluator.getFitness(candidate)));
            }
            evaluatedPopulation.addAll(evaluatedCandidates);
            latch.countDown();
        }
    }
}
