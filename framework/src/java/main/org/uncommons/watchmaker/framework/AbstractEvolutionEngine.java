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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.stats.PopulationDataSet;

/**
 * Base class for {@link EvolutionEngine} implementations.
 * @param <T> The type of entity evolved by the evolution engine.
 * @author Daniel Dyer
 */
public abstract class AbstractEvolutionEngine<T> implements EvolutionEngine<T>
{
    private final List<EvolutionObserver<T>> observers = new LinkedList<EvolutionObserver<T>>();
    private final Random rng;
    private final CandidateFactory<T> candidateFactory;
    private final EvolutionaryOperator<? super T> evolutionScheme;
    private final FitnessEvaluator<? super T> fitnessEvaluator;
    private final SelectionStrategy selectionStrategy;

    private long startTime;
    private int currentGenerationIndex;


    protected AbstractEvolutionEngine(CandidateFactory<T> candidateFactory,
                                      EvolutionaryOperator<? super T> evolutionScheme,
                                      FitnessEvaluator<? super T> fitnessEvaluator,
                                      SelectionStrategy selectionStrategy,
                                      Random rng)
    {
        this.candidateFactory = candidateFactory;
        this.evolutionScheme = evolutionScheme;
        this.fitnessEvaluator = fitnessEvaluator;
        this.selectionStrategy = selectionStrategy;
        this.rng = rng;
    }


    /**
     * Provides sub-classes with access to the fitness evaluator.
     * @return A reference to the fitness evaluator configured for this engine.
     */
    protected final FitnessEvaluator<? super T> getFitnessEvaluator()
    {
        return fitnessEvaluator;
    }


    @SuppressWarnings("unchecked")
    public T evolve(int populationSize,
                    int eliteCount,
                    int generationCount)
    {
        return evolve(populationSize,
                      eliteCount,
                      generationCount,
                      (Collection<T>) Collections.emptySet());
    }


    public T evolve(int populationSize,
                    int eliteCount,
                    int generationCount,
                    Collection<T> seedCandidates)
    {
        if (eliteCount < 0 || eliteCount >= populationSize)
        {
            throw new IllegalArgumentException("Elite count must be non-zero and less than population size.");
        }

        currentGenerationIndex = 0;
        startTime = System.currentTimeMillis();

        // Don't use the list returned by the factory, because the type might be too specific.
        // Instead copy the contents into a list of the desired type.
        List<T> population = new ArrayList<T>(candidateFactory.generateInitialPopulation(populationSize,
                                                                                         seedCandidates,
                                                                                         rng));
        // Calculate the fitness scores for each member of the population.
        List<EvaluatedCandidate<T>> evaluatedPopulation = evaluatePopulation(population);
        // Notify observers of the state of the population.
        notifyPopulationChange(getPopulationData(evaluatedPopulation));

        // This loop starts counting at 1, because the initial population counts as generation zero.
        for (int i = 1; i < generationCount; i++)
        {
            ++currentGenerationIndex;
            population = createNextGeneration(evaluatedPopulation, eliteCount);
            evaluatedPopulation = evaluatePopulation(population);
            // Notify observers of the state of the population.
            notifyPopulationChange(getPopulationData(evaluatedPopulation));
        }
        // Return the fittest candidate from the final generation.
        return evaluatedPopulation.get(0).getCandidate();
    }


    @SuppressWarnings("unchecked")
    public T evolve(int populationSize,
                    int eliteCount,
                    double targetFitness,
                    long timeout)
    {
        return evolve(populationSize,
                      eliteCount,
                      targetFitness,
                      timeout,
                      (Collection<T>) Collections.emptySet());
    }


    public T evolve(int populationSize,
                    int eliteCount,
                    double targetFitness,
                    long timeout,
                    Collection<T> seedCandidates)
    {
        if (eliteCount < 0 || eliteCount >= populationSize)
        {
            throw new IllegalArgumentException("Elite count must be non-zero and less than population size.");
        }

        currentGenerationIndex = 0;
        startTime = System.currentTimeMillis();
        long endTime = startTime + timeout;
        // Don't use the list returned by the factory, because the type might be too specific.
        // Instead copy the contents into a list of the desired type.
        List<T> population = new ArrayList<T>(candidateFactory.generateInitialPopulation(populationSize,
                                                                                         seedCandidates,
                                                                                         rng));
        List<EvaluatedCandidate<T>> evaluatedPopulation = evaluatePopulation(population);
        PopulationData<T> data = getPopulationData(evaluatedPopulation);
        // Notify observers of the state of the population.
        notifyPopulationChange(data);

        // Keep evolving until we match the target fitness or run out of time.
        while (data.getBestCandidateFitness() < targetFitness && System.currentTimeMillis() < endTime)
        {
            ++currentGenerationIndex;
            population = createNextGeneration(evaluatedPopulation, eliteCount);
            evaluatedPopulation = evaluatePopulation(population);
            data = getPopulationData(evaluatedPopulation);
            // Notify observers of the state of the population.
            notifyPopulationChange(data);
        }

        // Return the fittest candidate from the final generation.
        return evaluatedPopulation.get(0).getCandidate();
    }


    /**
     * Takes a population, assigns a fitness score to each member and returns
     * the members with their scores attached, sorted in descending order of
     * fitness (descending order of fitness score for natural scores, ascending
     * order of scores for non-natural scores).
     */
    protected abstract List<EvaluatedCandidate<T>> evaluatePopulation(List<T> population);


    /**
     * Evolve the specified evaluated population (the current generation).
     * and return the resultant population (the next generation).
     */
    private List<T> createNextGeneration(List<EvaluatedCandidate<T>> evaluatedPopulation,
                                         int eliteCount)
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
        assert population.size() == evaluatedPopulation.size() : "Population size is not consistent.";
        return population;
    }


    public void addEvolutionObserver(EvolutionObserver<T> observer)
    {
        observers.add(observer);
    }


    public void removeEvolutionObserver(EvolutionObserver<T> observer)
    {
        observers.remove(observer);
    }


    /**
     * Send the population data to all registered observers.
     */
    private void notifyPopulationChange(PopulationData<T> data)
    {
        for (EvolutionObserver<T> observer : observers)
        {
            observer.populationUpdate(data);
        }
    }


    /**
     * Gets data about the current population, including the fittest candidate
     * and statistics about the population as a whole.
     * @param evaluatedPopulation Population of candidate solutions with their
     * associated fitness scores.
     */
    private PopulationData<T> getPopulationData(List<EvaluatedCandidate<T>> evaluatedPopulation)
    {
        double[] fitnesses = new double[evaluatedPopulation.size()];
        int index = -1;
        for (EvaluatedCandidate<T> candidate : evaluatedPopulation)
        {
            fitnesses[++index] = candidate.getFitness();
        }
        PopulationDataSet stats = new PopulationDataSet(fitnesses);
        return new PopulationData<T>(evaluatedPopulation.get(0).getCandidate(),
                                     evaluatedPopulation.get(0).getFitness(),
                                     stats.getArithmeticMean(),
                                     stats.getStandardDeviation(),
                                     stats.getSize(),
                                     currentGenerationIndex,
                                     System.currentTimeMillis() - startTime);
    }
}
