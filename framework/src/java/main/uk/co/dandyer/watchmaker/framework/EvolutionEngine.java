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

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Generic evolutionary algorithm engine.
 * @author Daniel Dyer
 * @param <T> The type of entity that is to be evolved.
 */
public class EvolutionEngine<T>
{
    private final Set<EvolutionObserver> observers = new HashSet<EvolutionObserver>();
    private final Random rng;
    private final CandidateFactory<? extends T> candidateFactory;
    private final List<EvolutionaryProcess<? super T>> evolutionPipeline;
    private final FitnessEvaluator<? super T> fitnessEvaluator;
    private final SelectionStrategy selectionStrategy;


    public EvolutionEngine(CandidateFactory<? extends T> candidateFactory,
                           List<EvolutionaryProcess<? super T>> evolutionPipeline,
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
            List<Pair<T, Double>> evaluatedPopulation = new ArrayList<Pair<T, Double>>(population.size());
            for (T candidate : population)
            {
                evaluatedPopulation.add(new Pair<T, Double>(candidate,
                                                            fitnessEvaluator.getFitness(candidate)));
            }
            // Then select candidates that will be operated on to create the next generation.
            population = selectionStrategy.select(evaluatedPopulation, populationSize, rng);
            // Shuffle the collection before applying each operation so that the
            // evolution is not influenced by any ordering artifacts of the selection
            // strategy.
            Collections.shuffle(population, rng);
            // Then apply each evolutionary transformation to the selection in turn.
            for (EvolutionaryProcess<? super T> transform : evolutionPipeline)
            {
                population = transform.apply(population, rng);
            }
            assert population.size() == populationSize : "Population size is not consistent.";
        }

        // Return the best candidate from the final generation.
        T fittest = null;
        double bestFitness = 0;
        for (T candidate : population)
        {
            double fitness = fitnessEvaluator.getFitness(candidate);
            if (fitness > bestFitness)
            {
                fittest = candidate;
                bestFitness = fitness;
            }
        }
        return fittest;
    }


    public void addEvolutionObserver(EvolutionObserver observer)
    {
        observers.add(observer);
    }


    public void removeEvolutionObserver(EvolutionObserver observer)
    {
        observers.remove(observer);
    }
}
