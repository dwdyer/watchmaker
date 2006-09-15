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
package uk.co.dandyer.watchmaker.framework.selection;

import uk.co.dandyer.watchmaker.framework.Pair;
import uk.co.dandyer.watchmaker.framework.SelectionStrategy;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Base-class for selection strategies that use absolute fitness values to
 * determine a candidate's probability of being selected.
 * @author Daniel Dyer
 */
public abstract class FitnessProportionateSelection implements SelectionStrategy
{
    /**
     * {@inheritDoc}
     * <p>For fitness-proportionate selection this method first makes sure the
     * evaluated population is normalised (higher fitness scores for fitter
     * candidates) and then delegates to
     * {@link #fpSelect(java.util.List, int, java.util.Random)}.
     */
    public final <T> List<T> select(List<Pair<T, Double>> population,
                                    int selectionSize,
                                    Random rng)
    {
        assert !population.isEmpty() : "Cannot select from an empty population.";

        // Make sure we are dealing with a population with normalised (descending)
        // fitnesses.  Since the population is sorted by fitness this is just a
        // matter of comparing the first and last scores to see whether the list
        // is in ascending or descending order.
        boolean normalised = population.get(0).getSecond() >= population.get(population.size() - 1).getSecond();
        List<Pair<T, Double>> normalisedPopulation = normalised ? population : normaliseFitnesses(population);
        return fpSelect(normalisedPopulation, selectionSize, rng);
    }


    /**
     * Performs fitness-proportionate Selection on a population with normalised fitness
     * scores.
     */
    protected abstract <T> List<T> fpSelect(List<Pair<T, Double>> normalisedPopulation,
                                            int selectionSize,
                                            Random rng);

    /**
     * <p>Convert a population that is ranked using a lower-fitness-is-better
     * approach into a population in which the best candidate has the highest
     * score and the weakest candidate has the lowest.  In other words, the
     * ordering of the candidates remains the same but their scores are
     * converted so that they can be used for fitness-proportionate selection).</p>
     * <p>WARNING: Do not apply this method to a population that is already
     * normalised.</p>
     * @param population A sorted population in which the fittest individuals
     * have lower fitness scores than the weaker individuals.  That means the
     * first individual in the list both has the lowest fitness score and is
     * the fittest individual.
     */
    private <T> List<Pair<T, Double>> normaliseFitnesses(List<Pair<T, Double>> population)
    {
        List<Pair<T, Double>> normalisedPopulation = new ArrayList<Pair<T, Double>>(population.size());
        double totalFitnesses = 0;
        for (Pair<T, Double> candidate : population)
        {
            totalFitnesses += candidate.getSecond();
        }
        for (Pair<T, Double> candidate : population)
        {
            double normalisedFitness = normaliseFitness(candidate.getSecond(),
                                                        totalFitnesses,
                                                        population.size());
            normalisedPopulation.add(new Pair<T, Double>(candidate.getFirst(),
                                                         normalisedFitness));
        }
        return normalisedPopulation;
    }


    /**
     * Convert the fitness score for a candidate that is ranked using a
     * lower-fitness-is-better approach into a score on a
     * higher-fitness-is-better scale.
     */
    private double normaliseFitness(double fitness,
                                    double totalFitnesses,
                                    int populationSize)
    {
        return (1 - (fitness / totalFitnesses)) / (populationSize - 1);
    }
}
