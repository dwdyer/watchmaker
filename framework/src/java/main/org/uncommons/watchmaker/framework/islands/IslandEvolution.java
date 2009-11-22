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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionUtils;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

/**
 * An implementation of island evolution in which multiple independent populations are evolved in
 * parallel with occasional migration of individuals between islands.
 * @author Daniel Dyer
 */
public class IslandEvolution<T>
{
    private final List<EvolutionEngine<T>> islands;
    private final Migration migration;
    private final boolean naturalFitness;
    private final Random rng;

    private final List<EvolutionObserver<? super T>> observers = new LinkedList<EvolutionObserver<? super T>>();

    // Latest population data from each island.
    private final Map<Integer, PopulationData<? extends T>> islandData
        = Collections.synchronizedSortedMap(new TreeMap<Integer, PopulationData<? extends T>>());

    public IslandEvolution(int islandCount,
                           Migration migration,
                           CandidateFactory<T> candidateFactory,
                           EvolutionaryOperator<T> evolutionScheme,
                           FitnessEvaluator<? super T> fitnessEvaluator,
                           SelectionStrategy<? super T> selectionStrategy,
                           Random rng)
    {
        this(createIslands(islandCount,
                           candidateFactory,
                           evolutionScheme,
                           fitnessEvaluator,
                           selectionStrategy,
                           rng),
             migration,
             fitnessEvaluator.isNatural(),
             rng);
    }


    /**
     * Helper method used by the constructor to create the individual islands if they haven't
     * been provided already (via the other constructor).
     */
    private static <T> List<EvolutionEngine<T>> createIslands(int islandCount,
                                                              CandidateFactory<T> candidateFactory,
                                                              EvolutionaryOperator<T> evolutionScheme,
                                                              FitnessEvaluator<? super T> fitnessEvaluator,
                                                              SelectionStrategy<? super T> selectionStrategy,
                                                              Random rng)
    {
        List<EvolutionEngine<T>> islands = new ArrayList<EvolutionEngine<T>>(islandCount);
        for (int i = 0; i < islandCount; i++)
        {
            islands.add(EvolutionEngine.createGenerationalEvolutionEngine(candidateFactory,
                                                                          evolutionScheme,
                                                                          fitnessEvaluator,
                                                                          selectionStrategy,
                                                                          rng,
                                                                          true));
        }
        return islands;
    }


    public IslandEvolution(List<EvolutionEngine<T>> islands,
                           Migration migration,
                           boolean naturalFitness,
                           Random rng)
    {
        this.islands = islands;
        this.migration = migration;
        this.naturalFitness = naturalFitness;
        this.rng = rng;

        // Monitor population updates from each individual island.  Store the latest data
        // from each.
        for (int i = 0; i < islands.size(); i++)
        {
            final int islandID = i;
            islands.get(i).addEvolutionObserver(new EvolutionObserver<T>()
            {
                public void populationUpdate(PopulationData<? extends T> populationData)
                {
                    islandData.put(islandID, populationData);
                }
            });
        }
    }


    /**
     * <em>If you interrupt the request thread before this method returns, the
     * method will return prematurely (with the best individual found so far).
     * After returning in this way, the current thread's interrupted flag
     * will be set.  It is preferable to use an appropritate
     * {@link org.uncommons.watchmaker.framework.TerminationCondition} rather than interrupting the evolution in
     * this way.</em>
     */
    public T evolve(int populationSize,
                    int eliteCount,
                    int epochLength,
                    int migrantCount,
                    TerminationCondition... conditions)
    {
        long startTime = System.currentTimeMillis();
        ExecutorService threadPool = Executors.newFixedThreadPool(islands.size());

        List<List<T>> islandPopulations = new ArrayList<List<T>>(islands.size());
        for (int i = 0; i < islands.size(); i++)
        {
            islandPopulations.add(Collections.<T>emptyList());
        }

        List<EvaluatedCandidate<T>> evaluatedCombinedPopulation = new ArrayList<EvaluatedCandidate<T>>();
        PopulationData<T> data = null;
        int currentEpochIndex = 0;
        do
        {
            List<Callable<List<EvaluatedCandidate<T>>>> islandEpochs
                = new ArrayList<Callable<List<EvaluatedCandidate<T>>>>(islands.size());
            for (int i = 0; i < islands.size(); i++)
            {
                islandEpochs.add(new Epoch<T>(islands.get(i),
                                              populationSize,
                                              eliteCount,
                                              islandPopulations.get(i),
                                              new GenerationCount(epochLength)));
            }

            try
            {
                List<Future<List<EvaluatedCandidate<T>>>> futures = threadPool.invokeAll(islandEpochs);
                islandPopulations.clear();
                evaluatedCombinedPopulation.clear();
                for (Future<List<EvaluatedCandidate<T>>> future : futures)
                {
                    List<EvaluatedCandidate<T>> evaluatedIslandPopulation = future.get();
                    evaluatedCombinedPopulation.addAll(evaluatedIslandPopulation);
                    islandPopulations.add(EvolutionUtils.toCandidateList(evaluatedIslandPopulation));
                }
                ++currentEpochIndex;
                migration.migrate(islandPopulations, migrantCount, rng);
                data = EvolutionUtils.getPopulationData(evaluatedCombinedPopulation,
                                                        naturalFitness,
                                                        eliteCount,
                                                        currentEpochIndex,
                                                        startTime);
                notifyPopulationChange(data);
            }
            catch (InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
            catch (ExecutionException ex)
            {
                throw new IllegalStateException(ex);
            }
        } while (EvolutionUtils.shouldContinue(data, conditions) == null);
        EvolutionUtils.sortEvaluatedPopulation(evaluatedCombinedPopulation, naturalFitness);
        threadPool.shutdownNow();
        return evaluatedCombinedPopulation.get(0).getCandidate();
    }


    /**
     * <p>Adds an observer to the overall evolution.  Receives an update at the end of
     * each epoch describing the state of the combined population of all islands.</p>
     *
     * <p>Updates are dispatched synchronously on the request thread.  Observers should
     * complete their processing and return in a timely manner to avoid holding up
     * the evolution.</p>
     *
     * @param observer The callback that will be notified at the end of each epoch.
     *
     * @see #removeEvolutionObserver(EvolutionObserver)
     */
    public void addEvolutionObserver(EvolutionObserver<? super T> observer)
    {
        observers.add(observer);
    }


    /**
     * Remove the specified observer from the overall evolution.
     * @param observer The observer to remove (if it is registered).
     *
     * @see #addEvolutionObserver(EvolutionObserver)
     */
    public void removeEvolutionObserver(EvolutionObserver<? super T> observer)
    {
        observers.remove(observer);
    }


    /**
     * Send the population data to all registered observers.
     * @param data Information about the current state of the population.
     */
    private void notifyPopulationChange(PopulationData<T> data)
    {
        for (EvolutionObserver<? super T> observer : observers)
        {
            observer.populationUpdate(data);
        }
    }
}
