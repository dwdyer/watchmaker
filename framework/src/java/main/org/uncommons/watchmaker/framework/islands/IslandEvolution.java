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
import org.uncommons.maths.statistics.DataSet;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.ConcurrentEvolutionEngine;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

/**
 * @author Daniel Dyer
 */
public class IslandEvolution<T>
{
    private final List<EvolutionEngine<T>> islands;
    private final Migration migration;
    private final boolean naturalFitness;
    private final Random rng;

    // Latest population data from each island.
    private final Map<Integer, PopulationData<? extends T>> islandData
        = Collections.synchronizedSortedMap(new TreeMap<Integer, PopulationData<? extends T>>());

    private int currentEpochIndex;
    private long startTime;

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
            islands.add(new ConcurrentEvolutionEngine<T>(candidateFactory,
                                                         evolutionScheme,
                                                         fitnessEvaluator,
                                                         selectionStrategy,
                                                         rng));
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
     * {@inheritDoc}
     *
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
        startTime = System.currentTimeMillis();
        ExecutorService threadPool = Executors.newFixedThreadPool(islands.size());

        List<List<T>> islandPopulations = new ArrayList<List<T>>(islands.size());
        for (int i = 0; i < islands.size(); i++)
        {
            islandPopulations.add(Collections.<T>emptyList());
        }

        List<EvaluatedCandidate<T>> evaluatedCombinedPopulation = new ArrayList<EvaluatedCandidate<T>>();
        currentEpochIndex = 0;
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
                    islandPopulations.add(toCandidateList(evaluatedIslandPopulation));
                }
                ++currentEpochIndex;
                migration.migrate(islandPopulations, migrantCount, rng);
            }
            catch (InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
            catch (ExecutionException ex)
            {
                throw new IllegalStateException(ex);
            }
        } while (shouldContinue(getPopulationData(evaluatedCombinedPopulation, eliteCount), conditions) == null);
        sortEvaluatedPopulation(evaluatedCombinedPopulation);
        return evaluatedCombinedPopulation.get(0).getCandidate();
    }

    

    private List<TerminationCondition> shouldContinue(PopulationData<T> data,
                                                      TerminationCondition... conditions)
    {
        // If the thread has been interrupted, we should abort and return whatever
        // result we currently have.
        if (Thread.currentThread().isInterrupted())
        {
            return Collections.emptyList();
        }
        // Otherwise check the termination conditions for the evolution.
        List<TerminationCondition> satisfiedConditions = new LinkedList<TerminationCondition>();
        for (TerminationCondition condition : conditions)
        {
            if (condition.shouldTerminate(data))
            {
                satisfiedConditions.add(condition);
            }
        }
        return satisfiedConditions.isEmpty() ? null : satisfiedConditions;
    }



    /**
     * Convert a list of {@link EvaluatedCandidate}s into a simple list of candidates.
     * @param evaluatedCandidates The population of candidate objects to relieve of their
     * evaluation wrappers. 
     * @return The candidates, stripped of their fitness scores.
     */
    private List<T> toCandidateList(List<EvaluatedCandidate<T>> evaluatedCandidates)
    {
        List<T> candidates = new ArrayList<T>(evaluatedCandidates.size());
        for (EvaluatedCandidate<T> evaluatedCandidate : evaluatedCandidates)
        {
            candidates.add(evaluatedCandidate.getCandidate());
        }
        return candidates;
    }


    
    /**
     * Sorts an evaluated population in descending order of fitness
     * (descending order of fitness score for natural scores, ascending
     * order of scores for non-natural scores).
     * @param evaluatedPopulation The population to be sorted (in-place).
     */
    private void sortEvaluatedPopulation(List<EvaluatedCandidate<T>> evaluatedPopulation)
    {
        // Sort candidates in descending order according to fitness.
        if (naturalFitness) // Descending values for natural fitness.
        {
            Collections.sort(evaluatedPopulation, Collections.reverseOrder());
        }
        else // Ascending values for non-natural fitness.
        {
            Collections.sort(evaluatedPopulation);
        }
    }


    /**
     * Gets data about the current population, including the fittest candidate
     * and statistics about the population as a whole.
     * @param evaluatedPopulation Population of candidate solutions with their
     * associated fitness scores.
     * @param eliteCount The number of candidates preserved via elitism.
     * @return Statistics about the current generation of evolved individuals.
     */
    private PopulationData<T> getPopulationData(List<EvaluatedCandidate<T>> evaluatedPopulation,
                                                int eliteCount)
    {
        DataSet stats = new DataSet(evaluatedPopulation.size());
        for (EvaluatedCandidate<T> candidate : evaluatedPopulation)
        {
            stats.addValue(candidate.getFitness());
        }
        return new PopulationData<T>(evaluatedPopulation.get(0).getCandidate(),
                                     evaluatedPopulation.get(0).getFitness(),
                                     stats.getArithmeticMean(),
                                     stats.getStandardDeviation(),
                                     naturalFitness,
                                     stats.getSize(),
                                     eliteCount,
                                     currentEpochIndex,
                                     System.currentTimeMillis() - startTime);
    }
}
