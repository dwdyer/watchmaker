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
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
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
     * @see #evolve(int, double, long)
     */
    public T evolve(int populationSize,
                    int generationCount)
    {
        // Don't use the list returned by the factory, because the type might be too specific.
        // Instead copy the contents into a list of the desired type.
        List<T> population = new ArrayList<T>(candidateFactory.generateInitialPopulation(populationSize, rng));
        // Calculate the fitness scores for each member of the population.
        List<Pair<T, Double>> evaluatedPopulation = evaluatePopulation(population);

        // This loop starts counting at 1, because the initial population counts as generation zero.
        for (int i = 1; i < generationCount; i++)
        {
            // Create the next generation.
            population = createNextGeneration(evaluatedPopulation);
            // Then calculate the fitness scores for each member of the population.
            evaluatedPopulation = evaluatePopulation(population);
        }
        // Return the fittest candidate from the final generation.
        return evaluatedPopulation.get(0).getFirst();
    }


    /**
     * Runs the evolution until a target fitness score has been achieved by at least
     * one candidate solution.  To prevent this method from executing indefinitely,
     * a timeout is also specified.
     * @param populationSize The number of candidate solutions present in the population
     * at any point in time.
     * @param targetFitness The minimum satisfactory fitness score.  The evolution will
     * continue until a candidate is found with an equal or higher score, or the
     * execution times out.
     * @param timeout How long (in milliseconds) the evolution is allowed to run for
     * without finding a matching candidate.
     * @see #evolve(int, int) 
     */
    public T evolve(int populationSize,
                    double targetFitness,
                    long timeout)
    {
        long endTime = System.currentTimeMillis() + timeout;
        // Don't use the list returned by the factory, because the type might be too specific.
        // Instead copy the contents into a list of the desired type.
        List<T> population = new ArrayList<T>(candidateFactory.generateInitialPopulation(populationSize, rng));

        // Calculate the fitness scores for each member of the population.
        List<Pair<T, Double>> evaluatedPopulation = evaluatePopulation(population);

        // Then keep evolving until we match the target fitness or run out of time.
        double bestFitness = evaluatedPopulation.get(0).getSecond();
        while (bestFitness < targetFitness && System.currentTimeMillis() < endTime)
        {
            population = createNextGeneration(evaluatedPopulation);
            evaluatedPopulation = evaluatePopulation(population);
            bestFitness = evaluatedPopulation.get(0).getSecond();
        }

        // Return the fittest candidate from the final generation.
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
     * Evolve the specified evaluated population (the current generation).
     * and return the resultant population (the next generation).
     */
    private List<T> createNextGeneration(List<Pair<T, Double>> evaluatedPopulation)
    {
        // Then select candidates that will be operated on to create the next generation.
        List<T> population = selectionStrategy.select(evaluatedPopulation, evaluatedPopulation.size(), rng);
        // Shuffle the collection before applying each operation so that the
        // evolution is not influenced by any ordering artifacts of the selection
        // strategy.
        Collections.shuffle(population, rng);
        // Then apply each evolutionary transformation to the selection in turn.
        for (EvolutionaryOperator<? super T> transform : evolutionPipeline)
        {
            population = transform.apply(population, rng);
        }
        assert population.size() == evaluatedPopulation.size() : "Population size is not consistent.";
        return population;
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


    /**
     * Adds a listener to receive status updates on the evolution progress.
     * @see #removeEvolutionObserver(EvolutionObserver<? super T>)
     */
    public void addEvolutionObserver(EvolutionObserver<? super T> observer)
    {
        observers.add(observer);
    }


    /**
     * Removes an evolution progress listener.
     * @see #addEvolutionObserver(EvolutionObserver<? super T>)
     */
    public void removeEvolutionObserver(EvolutionObserver<? super T> observer)
    {
        observers.remove(observer);
    }
}
