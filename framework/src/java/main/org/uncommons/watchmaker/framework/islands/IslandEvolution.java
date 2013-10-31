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
package org.uncommons.watchmaker.framework.islands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
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
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

/**
 * An implementation of island evolution in which multiple independent populations are evolved in
 * parallel with periodic migration of individuals between islands.
 * @param <T> The type of entity that is to be evolved.
 * @author Daniel Dyer
 */
public class IslandEvolution<T>
{
    private final List<EvolutionEngine<T>> islands;
    private final Migration<? super T> migration;
    private final boolean naturalFitness;
    private final Random rng;

    private final Set<IslandEvolutionObserver<? super T>> observers
        = new CopyOnWriteArraySet<IslandEvolutionObserver<? super T>>();

    private List<TerminationCondition> satisfiedTerminationConditions;


    /**
     * Create an island system with the specified number of identically-configured islands.
     * If you want more fine-grained control over the configuration of each island, use the
     * {@link #IslandEvolution(List, Migration, boolean, Random)} constructor, which accepts
     * a list of pre-created islands (each is an instance of {@link EvolutionEngine}). 
     * @param islandCount The number of separate islands that will be part of the system.
     * @param migration A migration strategy for moving individuals between islands at the
     * end of an epoch.
     * @param candidateFactory Generates the initial population for each island.
     * @param evolutionScheme The evolutionary operator, or combination of evolutionary operators,
     * used on each island.
     * @param fitnessEvaluator The fitness function used on each island.
     * @param selectionStrategy The selection strategy used on each island.
     * @param rng A source of randomness, used by all islands.
     * @see #IslandEvolution(List, Migration, boolean, Random) 
     */
    public IslandEvolution(int islandCount,
                           Migration<? super T> migration,
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
     * Create an island evolution system from a list of pre-configured islands.  This constructor
     * gives more control over the configuration of individual islands than the alternative constructor.
     * The other constructor should be used where possible to avoid having to explicitly create each
     * island.
     * @param islands A list of pre-configured islands.
     * @param migration A migration strategy for moving individuals between islands at the
     * end of an epoch.
     * @param naturalFitness If true, indicates that higher fitness values mean fitter
     * individuals.  If false, indicates that fitter individuals will have lower scores.
     * @param rng A source of randomness, used by all islands.
     * @see #IslandEvolution(int, Migration, CandidateFactory, EvolutionaryOperator, FitnessEvaluator,
     * SelectionStrategy, Random)
     */
    public IslandEvolution(List<EvolutionEngine<T>> islands,
                           Migration<? super T> migration,
                           boolean naturalFitness,
                           Random rng)
    {
        this.islands = islands;
        this.migration = migration;
        this.naturalFitness = naturalFitness;
        this.rng = rng;

        for (int i = 0; i < islands.size(); i++)
        {
            final int islandIndex = i;
            EvolutionEngine<T> island = islands.get(islandIndex);
            island.addEvolutionObserver(new EvolutionObserver<T>()
            {
                public void populationUpdate(PopulationData<? extends T> populationData)
                {
                    for (IslandEvolutionObserver<? super T> islandObserver : observers)
                    {
                        islandObserver.islandPopulationUpdate(islandIndex, populationData);
                    }
                }
            });
        }
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
            GenerationalEvolutionEngine<T> island = new GenerationalEvolutionEngine<T>(candidateFactory,
                                                                                       evolutionScheme,
                                                                                       fitnessEvaluator,
                                                                                       selectionStrategy,
                                                                                       rng);
            island.setSingleThreaded(true); // Don't need fine-grained concurrency when each island is on a separate thread.
            islands.add(island);
        }
        return islands;
    }


    /**
     * <p>Start the evolutionary process on each island and return the fittest candidate so far at the point
     * any of the termination conditions is satisfied.</p>
     *
     * <p><em>If you interrupt the request thread before this method returns, the
     * method will return prematurely (with the best individual found so far).
     * After returning in this way, the current thread's interrupted flag
     * will be set.  It is preferable to use an appropritate
     * {@link org.uncommons.watchmaker.framework.TerminationCondition} rather than interrupting the evolution in
     * this way.</em></p>
     *
     * @param populationSize The population size <em>for each island</em>.  Therefore, if you have 5 islands,
     * setting this parameter to 200 will result in 1000 individuals overall, 200 on each island.
     * @param eliteCount The number of candidates preserved via elitism <em>on each island</em>.  In elitism,
     * a sub-set of the population with the best fitness scores are preserved unchanged in
     * the subsequent generation.  Candidate solutions that are preserved unchanged through
     * elitism remain eligible for selection for breeding the remainder of the next generation.
     * This value must be non-negative and less than the population size.  A value of zero
     * means that no elitism will be applied.
     * @param epochLength The number of generations that make up an epoch.  Islands evolve independently for
     * this number of generations and then migration occurs at the end of the epoch and the next epoch starts.
     * @param migrantCount The number of individuals that will be migrated from each island at the end of each
     * epoch.
     * @param conditions One or more conditions that may cause the evolution to terminate.
     * @return The fittest solution found by the evolutionary process on any of the islands.
     */
    public T evolve(int populationSize,
                    int eliteCount,
                    int epochLength,
                    int migrantCount,
                    TerminationCondition... conditions)
    {
        ExecutorService threadPool = Executors.newFixedThreadPool(islands.size());
        List<List<T>> islandPopulations = new ArrayList<List<T>>(islands.size());
        List<EvaluatedCandidate<T>> evaluatedCombinedPopulation = new ArrayList<EvaluatedCandidate<T>>();

        PopulationData<T> data = null;
        List<TerminationCondition> satisfiedConditions = null;
        int currentEpochIndex = 0;
        long startTime = System.currentTimeMillis();
        while (satisfiedConditions == null)
        {
            List<Callable<List<EvaluatedCandidate<T>>>> islandEpochs = createEpochTasks(populationSize,
                                                                                        eliteCount,
                                                                                        epochLength,
                                                                                        islandPopulations);
            try
            {
                List<Future<List<EvaluatedCandidate<T>>>> futures = threadPool.invokeAll(islandEpochs);

                evaluatedCombinedPopulation.clear();
                List<List<EvaluatedCandidate<T>>> evaluatedPopulations
                    = new ArrayList<List<EvaluatedCandidate<T>>>(islands.size());
                for (Future<List<EvaluatedCandidate<T>>> future : futures)
                {
                    List<EvaluatedCandidate<T>> evaluatedIslandPopulation = future.get();
                    evaluatedCombinedPopulation.addAll(evaluatedIslandPopulation);
                    evaluatedPopulations.add(evaluatedIslandPopulation);
                }

                migration.migrate(evaluatedPopulations, migrantCount, rng);

                EvolutionUtils.sortEvaluatedPopulation(evaluatedCombinedPopulation, naturalFitness);
                data = EvolutionUtils.getPopulationData(evaluatedCombinedPopulation,
                                                        naturalFitness,
                                                        eliteCount,
                                                        currentEpochIndex,
                                                        startTime);
                notifyPopulationChange(data);

                islandPopulations.clear();
                for (List<EvaluatedCandidate<T>> evaluatedPopulation : evaluatedPopulations)
                {
                    islandPopulations.add(toCandidateList(evaluatedPopulation));
                }
                ++currentEpochIndex;
            }
            catch (InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
            catch (ExecutionException ex)
            {
                throw new IllegalStateException(ex);
            }
            satisfiedConditions = EvolutionUtils.shouldContinue(data, conditions);
        }
        threadPool.shutdownNow();

        this.satisfiedTerminationConditions = satisfiedConditions;
        return evaluatedCombinedPopulation.get(0).getCandidate();
    }


    /**
     * Create the concurrently-executed tasks that perform evolution on each island.
     */
    private List<Callable<List<EvaluatedCandidate<T>>>> createEpochTasks(int populationSize,
                                                                         int eliteCount,
                                                                         int epochLength,
                                                                         List<List<T>> islandPopulations)
    {
        List<Callable<List<EvaluatedCandidate<T>>>> islandEpochs
            = new ArrayList<Callable<List<EvaluatedCandidate<T>>>>(islands.size());
        for (int i = 0; i < islands.size(); i++)
        {
            islandEpochs.add(new Epoch<T>(islands.get(i),
                                          populationSize,
                                          eliteCount,
                                          islandPopulations.isEmpty() ? Collections.<T>emptyList() : islandPopulations.get(i),
                                          new GenerationCount(epochLength)));
        }
        return islandEpochs;
    }


    /**
     * Convert a list of {@link EvaluatedCandidate}s into a simple list of candidates.
     * @param evaluatedCandidates The population of candidate objects to relieve of their
     * evaluation wrappers.
     * @param <T> The type of entity that is being evolved.
     * @return The candidates, stripped of their fitness scores.
     */
    private static <T> List<T> toCandidateList(List<EvaluatedCandidate<T>> evaluatedCandidates)
    {
        List<T> candidates = new ArrayList<T>(evaluatedCandidates.size());
        for (EvaluatedCandidate<T> evaluatedCandidate : evaluatedCandidates)
        {
            candidates.add(evaluatedCandidate.getCandidate());
        }
        return candidates;
    }


    /**
     * <p>Returns a list of all {@link TerminationCondition}s that are satisfied by the current
     * state of the island evolution.  Usually this list will contain only one item, but it
     * is possible that mutliple termination conditions will become satisfied at the same
     * time.  In this case the condition objects in the list will be in the same order that
     * they were specified when passed to the engine.</p>
     *
     * <p>If the evolution has not yet terminated (either because it is still in progress or
     * because it hasn't even been started) then an IllegalStateException will be thrown.</p>
     *
     * <p>If the evolution terminated because the request thread was interrupted before any
     * termination conditions were satisfied then this method will return an empty list.</p>
     *
     * @throws IllegalStateException If this method is invoked on an island system before
     * evolution is started or while it is still in progress.
     *
     * @return A list of statisfied conditions.  The list is guaranteed to be non-null.  The
     * list may be empty because it is possible for evolution to terminate without any conditions
     * being matched.  The only situation in which this occurs is when the request thread is
     * interrupted.
     */
    public List<TerminationCondition> getSatisfiedTerminationConditions()
    {
        if (satisfiedTerminationConditions == null)
        {
            throw new IllegalStateException("EvolutionEngine has not terminated.");
        }
        else
        {
            return Collections.unmodifiableList(satisfiedTerminationConditions);
        }
    }


    /**
     * <p>Adds an observer to the evolution.  Observers will receives two types of updates:
     * updates from each individual island at the end of each generation, and updates for
     * the combined global population at the end of each epoch.</p>
     *
     * <p>Updates are dispatched synchronously on the request thread.  Observers should
     * complete their processing and return in a timely manner to avoid holding up
     * the evolution.</p>
     *
     * @param observer The callback that will be notified at the end of each generation and epoch.
     *
     * @see #removeEvolutionObserver(IslandEvolutionObserver)
     */
    public void addEvolutionObserver(final IslandEvolutionObserver<? super T> observer)
    {
        observers.add(observer);
    }


    /**
     * Remove the specified observer.
     * @param observer The observer to remove (if it is registered).
     *
     * @see #addEvolutionObserver(IslandEvolutionObserver)
     */
    public void removeEvolutionObserver(final IslandEvolutionObserver<? super T> observer)
    {
        observers.remove(observer);
    }


    /**
     * Send the population data to all registered observers.
     * @param data Information about the current state of the population.
     */
    private void notifyPopulationChange(PopulationData<T> data)
    {
        for (IslandEvolutionObserver<? super T> observer : observers)
        {
            observer.populationUpdate(data);
        }
    }
}
