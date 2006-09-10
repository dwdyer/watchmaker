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
package uk.co.dandyer.watchmaker.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Comparator;
import uk.co.dandyer.maths.stats.PopulationDataSet;

/**
 * Generic evolutionary algorithm engine.
 * @author Daniel Dyer
 * @param <T> The type of entity that is to be evolved.
 */
public class EvolutionEngine<T>
{
    private static final Comparator<Pair<?, Double>> FITNESS_COMPARATOR = new CandidateFitnessComparator();

    private final List<EvolutionObserver<? super T>> observers = new LinkedList<EvolutionObserver<? super T>>();
    private final Random rng;
    private final CandidateFactory<? extends T> candidateFactory;
    private final List<EvolutionaryOperator<? super T>> evolutionPipeline;
    private final FitnessEvaluator<? super T> fitnessEvaluator;
    private final SelectionStrategy selectionStrategy;


    public EvolutionEngine(CandidateFactory<? extends T> candidateFactory,
                           List<EvolutionaryOperator<? super T>> evolutionPipeline,
                           FitnessEvaluator<? super T> fitnessEvaluator,
                           SelectionStrategy selectionStrategy,
                           Random rng)
    {
        this.candidateFactory = candidateFactory;
        this.evolutionPipeline = evolutionPipeline;
        this.fitnessEvaluator = fitnessEvaluator;
        this.selectionStrategy = selectionStrategy;
        this.rng = rng;
    }


    /**
     * @param populationSize The number of candidate solutions present in the population
     * at any point in time.
     * @param generationCount The number of iterations to perform (including the
     * creation and evaluation of the initial population).
     * @return The best solution found by evolutionary process.
     */
    public T evolve(int populationSize,
                    int generationCount)
    {
        // Don't use the list returned by the factory, because the type might be too specific.
        // Instead copy the contents into a list of the desired type.
        List<T> population = new ArrayList<T>(candidateFactory.generateInitialPopulation(populationSize, rng));
        for (int i = 1; i < generationCount; i++)
        {
            // Calculate the fitness scores for each member of the population.
            List<Pair<T, Double>> evaluatedPopulation = evaluatePopulation(population);
            // Then select candidates that will be operated on to create the next generation.
            population = selectionStrategy.select(evaluatedPopulation, populationSize, rng);
            // Shuffle the collection before applying each operation so that the
            // evolution is not influenced by any ordering artifacts of the selection
            // strategy.
            Collections.shuffle(population, rng);
            // Then apply each evolutionary transformation to the selection in turn.
            for (EvolutionaryOperator<? super T> transform : evolutionPipeline)
            {
                population = transform.apply(population, rng);
            }
            assert population.size() == populationSize : "Population size is not consistent.";
        }

        // Return the fittest candidate from the final generation.
        List<Pair<T, Double>> evaluatedPopulation = evaluatePopulation(population);
        return evaluatedPopulation.get(0).getFirst();
    }


    /**
     * Takes a population, assigns a fitness score to each member and returns
     * the members with their scores attached, sorted in descending order.
     */
    private List<Pair<T, Double>> evaluatePopulation(List<T> population)
    {
        List<Pair<T, Double>> evaluatedPopulation = new ArrayList<Pair<T, Double>>(population.size());
        for (T candidate : population)
        {
            evaluatedPopulation.add(new Pair<T, Double>(candidate,
                                                        fitnessEvaluator.getFitness(candidate)));
        }
        // Sort candidates in descending order according to fitness.
        Collections.sort(evaluatedPopulation, FITNESS_COMPARATOR);
        // Notify observers of the state of the population.
        if (!observers.isEmpty()) // No point calculating stats for nobody.
        {
            notifyObservers(getPopulationData(evaluatedPopulation));
        }
        return evaluatedPopulation;
    }


    /**
     * Gets data about the current population, including the fittest candidate
     * and statistics about the population as a whole.
     * @param evaluatedPopulation Population of candidate solutions with their
     * associated fitness scores.
     */
    private PopulationData<T> getPopulationData(List<Pair<T, Double>> evaluatedPopulation)
    {
        double[] fitnesses = new double[evaluatedPopulation.size()];
        int index = -1;
        for (Pair<T, Double> candidate : evaluatedPopulation)
        {
            fitnesses[++index] = candidate.getSecond();
        }
        PopulationDataSet stats = new PopulationDataSet(fitnesses);
        return new PopulationData<T>(evaluatedPopulation.get(0).getFirst(),
                                     evaluatedPopulation.get(0).getSecond(),
                                     stats.getArithmeticMean(),
                                     stats.getStandardDeviation(),
                                     stats.getSize());
    }


    /**
     * Send the population data to all registered observers.
     */
    private void notifyObservers(PopulationData<T> data)
    {
        for (EvolutionObserver<? super T> observer : observers)
        {
            observer.populationUpdate(data);
        }
    }


    public void addEvolutionObserver(EvolutionObserver<? super T> observer)
    {
        observers.add(observer);
    }


    public void removeEvolutionObserver(EvolutionObserver<? super T> observer)
    {
        observers.remove(observer);
    }
}
